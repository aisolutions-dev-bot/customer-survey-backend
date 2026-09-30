package com.example.survey.tenancy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Binds the database of the company a request names to the request thread.
 *
 * <p>A request that names no company is left unbound, which is the legacy path
 * for links already emailed with only {@code group_id}: the request runs
 * against the default datasource exactly as it did before tenant routing
 * existed.
 *
 * <p>A request whose company cannot be resolved to a database fails rather
 * than falling through unbound, because that would answer it from the default
 * database instead of the company's own. The company id is a routing hint
 * taken from a link the company itself was sent, not a credential.
 */
public class TenantResolutionFilter extends OncePerRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(TenantResolutionFilter.class);

  private static final String COMPANY_ID_QUERY_PARAMETER = "c";
  private static final String COMPANY_ID_HEADER = "X-Company-Id";

  private final TenantResolver tenantResolver;
  private final ObjectMapper objectMapper;

  public TenantResolutionFilter(TenantResolver tenantResolver, ObjectMapper objectMapper) {
    this.tenantResolver = tenantResolver;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String companyId = readCompanyId(request);
    if (companyId == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      TenantContext.setTenant(tenantResolver.resolveFromCompanyId(companyId));
    } catch (CompanyDbLookupException e) {
      LOG.error("tenant-resolution: failing {} {} rather than serving it from the default database",
          request.getMethod(), request.getRequestURI(), e);
      writeErrorResponse(response, HttpStatus.SERVICE_UNAVAILABLE,
          "Company cannot be resolved", e.getMessage());
      return;
    }

    try {
      filterChain.doFilter(request, response);
    } finally {
      // Request threads are pooled; a tenant left bound here is inherited by
      // whichever request runs on this thread next.
      TenantContext.clear();
    }
  }

  /**
   * The company id the request names, preferring the {@code X-Company-Id}
   * header over the {@code c} query parameter. Both spellings are accepted
   * because emailed links carry the company as the {@code c} query parameter
   * while the application sends it as a header.
   *
   * @return the company id, or null when the request names no company
   */
  private String readCompanyId(HttpServletRequest request) {
    String headerCompanyId = trimToNull(request.getHeader(COMPANY_ID_HEADER));
    return headerCompanyId != null ? headerCompanyId : trimToNull(request.getParameter(COMPANY_ID_QUERY_PARAMETER));
  }

  private String trimToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  private void writeErrorResponse(
      HttpServletResponse response, HttpStatus status, String error, String message) throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    objectMapper.writeValue(response.getWriter(), Map.of(
        "status", status.value(),
        "error", error,
        "message", message,
        "timestamp", LocalDateTime.now().toString()));
  }
}
