package com.example.survey.tenancy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import jakarta.annotation.PreDestroy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool.PoolInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Builds and owns the per-tenant HikariCP pools.
 *
 * <p>A tenant pool clones the default pool's configuration and swaps only the
 * database name, so no per-company connection settings exist anywhere. The
 * clone keeps the default pool size, so a service instance can hold
 * ({@code maxCachedPools} + 1) x {@code maximumPoolSize} MySQL connections;
 * lower {@code spring.datasource.hikari.maximum-pool-size} before raising the
 * pool-cache bound.
 *
 * <p>Pools live in an access-ordered map bounded by {@code maxCachedPools}: the
 * least recently used pool is closed and dropped once the bound is exceeded,
 * and a sweep closes any pool unused for {@code idleEviction}. Eviction closes
 * the pool so its connections are released to MySQL rather than leaked by a
 * dropped reference.
 */
@Component
public class CompanyDataSourceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyDataSourceRegistry.class);

    private static final String TENANT_POOL_NAME_PREFIX = "survey-tenant-";

    private final HikariDataSource defaultDataSource;
    private final int maxCachedPools;
    private final Duration idleEviction;
    private final Map<String, CachedPool> poolsByDatabaseName = new LinkedHashMap<>(16, 0.75f, true);

    public CompanyDataSourceRegistry(
            @Qualifier("defaultDataSource") HikariDataSource defaultDataSource,
            @Value("${aisolutions.tenancy.pool-cache.max-size:5}") int maxCachedPools,
            @Value("${aisolutions.tenancy.pool-cache.idle-eviction-minutes:15}") int idleEvictionMinutes) {
        this.defaultDataSource = defaultDataSource;
        this.maxCachedPools = maxCachedPools;
        this.idleEviction = Duration.ofMinutes(idleEvictionMinutes);
    }

    /**
     * The pool for {@code databaseName}, built on first use and evicted once the
     * cache bound or the idle timeout is reached. Callers must pass a resolved
     * database name; a company without one is routed to the default datasource
     * by {@link TenantRoutingDataSource} before reaching this method.
     */
    public HikariDataSource dataSourceFor(String databaseName) {
        synchronized (poolsByDatabaseName) {
            CachedPool cachedPool = poolsByDatabaseName.get(databaseName);
            if (cachedPool != null) {
                cachedPool.markUsedAt(System.currentTimeMillis());
                return cachedPool.dataSource();
            }
            HikariDataSource createdPool = buildPoolForDatabase(databaseName);
            poolsByDatabaseName.put(databaseName, new CachedPool(createdPool, System.currentTimeMillis()));
            evictLeastRecentlyUsedOverflow();
            return createdPool;
        }
    }

    /** Returns the default database and every active tenant pool for outbox relaying. */
    public List<DataSource> dataSourcesForNotificationRelay() {
        List<DataSource> dataSources = new ArrayList<>();
        dataSources.add(defaultDataSource);
        synchronized (poolsByDatabaseName) {
            poolsByDatabaseName.values().stream().map(CachedPool::dataSource).forEach(dataSources::add);
        }
        return List.copyOf(dataSources);
    }

    /**
     * Closes pools that have gone unused for the idle-eviction interval, so a
     * tenant that stops receiving traffic stops holding MySQL connections even
     * though nothing touches its entry in the cache.
     */
    @Scheduled(fixedDelayString = "${aisolutions.tenancy.pool-cache.sweep-interval-ms:60000}")
    public void closeIdleDataSources() {
        long idleCutoff = System.currentTimeMillis() - idleEviction.toMillis();
        List<HikariDataSource> idlePools = new ArrayList<>();
        synchronized (poolsByDatabaseName) {
            Iterator<Map.Entry<String, CachedPool>> pools =
                    poolsByDatabaseName.entrySet().iterator();
            while (pools.hasNext()) {
                Map.Entry<String, CachedPool> entry = pools.next();
                if (entry.getValue().lastUsedAtMillis() <= idleCutoff) {
                    pools.remove();
                    idlePools.add(entry.getValue().dataSource());
                }
            }
        }
        idlePools.forEach(pool -> closePool(pool, "idle"));
    }

    /** Closes every tenant pool on shutdown, leaving the default pool to Spring. */
    @PreDestroy
    public void closeAllDataSources() {
        List<HikariDataSource> pools;
        synchronized (poolsByDatabaseName) {
            pools = poolsByDatabaseName.values().stream()
                    .map(CachedPool::dataSource)
                    .toList();
            poolsByDatabaseName.clear();
        }
        pools.forEach(pool -> closePool(pool, "shutdown"));
    }

    /**
     * Drops least-recently-used pools until the cache is back within its bound.
     * An evicted pool stops handing out new connections but lets connections
     * already checked out by in-flight requests finish, so a request that is
     * still running is never cut off mid-transaction.
     */
    private void evictLeastRecentlyUsedOverflow() {
        while (poolsByDatabaseName.size() > maxCachedPools) {
            Iterator<Map.Entry<String, CachedPool>> pools =
                    poolsByDatabaseName.entrySet().iterator();
            Map.Entry<String, CachedPool> leastRecentlyUsed = pools.next();
            pools.remove();
            LOG.info(
                    "company-datasource-registry: evicting pool for database {} (cache bound {})",
                    leastRecentlyUsed.getKey(),
                    maxCachedPools);
            closePool(leastRecentlyUsed.getValue().dataSource(), "least-recently-used");
        }
    }

    private HikariDataSource buildPoolForDatabase(String databaseName) {
        HikariConfig tenantConfig = new HikariConfig();
        defaultDataSource.copyStateTo(tenantConfig);
        tenantConfig.setJdbcUrl(rewriteJdbcUrlDatabase(defaultDataSource.getJdbcUrl(), databaseName));
        tenantConfig.setPoolName(TENANT_POOL_NAME_PREFIX + databaseName);
        LOG.info("company-datasource-registry: opening pool for database {}", databaseName);
        try {
            return new HikariDataSource(tenantConfig);
        } catch (PoolInitializationException e) {
            // HikariCP opens the pool in its constructor and reports the failure
            // without naming which database it could not reach.
            throw new IllegalStateException(
                    "Could not open a datasource for database " + databaseName
                            + " using the default datasource settings",
                    e);
        }
    }

    /**
     * Replaces the database segment of a JDBC URL, keeping host, port, driver
     * parameters and query string exactly as the default datasource defines
     * them. Credentials are never rewritten, so every tenant pool connects with
     * the service's existing grants.
     */
    private String rewriteJdbcUrlDatabase(String defaultJdbcUrl, String databaseName) {
        int queryStart = defaultJdbcUrl.indexOf('?');
        String urlWithoutQuery = queryStart >= 0 ? defaultJdbcUrl.substring(0, queryStart) : defaultJdbcUrl;
        String query = queryStart >= 0 ? defaultJdbcUrl.substring(queryStart) : "";
        int databaseSegmentStart = urlWithoutQuery.lastIndexOf('/');
        if (databaseSegmentStart < 0) {
            throw new IllegalStateException(
                    "Default JDBC URL has no database segment to replace, so a tenant datasource cannot be built");
        }
        return urlWithoutQuery.substring(0, databaseSegmentStart + 1) + databaseName + query;
    }

    /** Closes a tenant pool after its cache entry has been removed.
     *
     * @param pool tenant connection pool
     * @param reason cache-removal reason used for logging
     */
    private void closePool(HikariDataSource pool, String reason) {
        pool.close();
        LOG.debug("company-datasource-registry: closed {} pool {}", reason, pool.getPoolName());
    }

    /** A cached pool together with the time it was last routed a request. */
    private static final class CachedPool {

        private final HikariDataSource dataSource;
        private long lastUsedAtMillis;

        private CachedPool(HikariDataSource dataSource, long lastUsedAtMillis) {
            this.dataSource = dataSource;
            this.lastUsedAtMillis = lastUsedAtMillis;
        }

        private HikariDataSource dataSource() {
            return dataSource;
        }

        private long lastUsedAtMillis() {
            return lastUsedAtMillis;
        }

        private void markUsedAt(long usedAtMillis) {
            this.lastUsedAtMillis = usedAtMillis;
        }
    }
}
