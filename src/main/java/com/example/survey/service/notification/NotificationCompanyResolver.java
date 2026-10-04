package com.example.survey.service.notification;

import com.example.survey.tenancy.TenantContext;
import org.springframework.stereotype.Component;

/**
 * Resolves the company that owns a delivery request from the request's bound tenant.
 *
 * <p>Delegates to {@link #resolveRequiredCompanyId}, which reads
 * {@link TenantContext#currentCompanyId()} and fails closed when the request resolved
 * no company. It never falls back to a shared default company, because staging under
 * the wrong company would deliver with another tenant's settings.
 */
@Component
public class NotificationCompanyResolver {

    /**
     * Returns the company the calling request resolved to.
     *
     * @return the tenant company id, never blank
     * @throws IllegalStateException when no tenant is bound to the calling thread
     */
    public String resolveRequiredCompanyId() {
        String companyId = TenantContext.currentCompanyId();
        if (companyId == null || companyId.isBlank()) {
            throw new IllegalStateException(
                    "No tenant is bound to the request thread, so a notification delivery request cannot be staged");
        }
        return companyId;
    }
}
