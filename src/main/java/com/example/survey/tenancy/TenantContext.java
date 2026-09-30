package com.example.survey.tenancy;

/**
 * Thread-bound holder for the tenant a request resolved to.
 *
 * <p>Request threads are pooled, so a value left bound to a thread is read by
 * whichever request runs on that thread next and routes it to another
 * company's database. Every setter must be paired with {@link #clear()} in a
 * finally block.
 */
public final class TenantContext {

  private static final ThreadLocal<TenantIdentity> CURRENT_TENANT = new ThreadLocal<>();

  private TenantContext() {
  }

  /**
   * The tenant a request resolved to. {@code databaseName} is null when the
   * company has no database of its own, which means the default datasource.
   */
  public record TenantIdentity(String companyId, String databaseName) {
  }

  /** Binds {@code identity} to the calling thread. */
  public static void setTenant(TenantIdentity identity) {
    CURRENT_TENANT.set(identity);
  }

  /** Unbinds the calling thread's tenant. Safe to call when nothing is bound. */
  public static void clear() {
    CURRENT_TENANT.remove();
  }

  /**
   * The database the calling thread's request must be routed to, or null when
   * no tenant is bound or the company has no database of its own. Null is the
   * routing datasource's signal to use the default datasource.
   */
  public static String currentDatabaseName() {
    TenantIdentity identity = CURRENT_TENANT.get();
    return identity != null ? identity.databaseName() : null;
  }

  /** The company the calling thread's request resolved to, or null when unset. */
  public static String currentCompanyId() {
    TenantIdentity identity = CURRENT_TENANT.get();
    return identity != null ? identity.companyId() : null;
  }
}
