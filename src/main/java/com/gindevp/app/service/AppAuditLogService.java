package com.gindevp.app.service;

import com.gindevp.app.domain.AppAuditLog;
import com.gindevp.app.repository.AppAuditLogRepository;
import com.gindevp.app.security.SecurityUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppAuditLogService {

    private static final Logger LOG = LoggerFactory.getLogger(AppAuditLogService.class);

    private final AppAuditLogRepository appAuditLogRepository;

    public AppAuditLogService(AppAuditLogRepository appAuditLogRepository) {
        this.appAuditLogRepository = appAuditLogRepository;
    }

    @Transactional(readOnly = true)
    public Page<AppAuditLog> findAll(Pageable pageable) {
        return appAuditLogRepository.findAllByOrderByOccurredAtDesc(pageable);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(String httpMethod, String uriPath, int responseStatus) {
        try {
            AppAuditLog row = new AppAuditLog();
            row.setOccurredAt(Instant.now());
            row.setHttpMethod(httpMethod);
            row.setUriPath(uriPath != null && uriPath.length() > 500 ? uriPath.substring(0, 500) : uriPath);
            row.setResponseStatus(responseStatus);
            row.setLogin(SecurityUtils.getCurrentUserLogin().orElse("anonymous"));
            appAuditLogRepository.save(row);
        } catch (Exception e) {
            LOG.warn("Audit log skipped: {}", e.getMessage());
        }
    }

    /**
     * Ghi nhận sự kiện nghiệp vụ (httpMethod = BIZ), không gắn mã HTTP.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordBusiness(String actionKey, String detailText) {
        try {
            AppAuditLog row = new AppAuditLog();
            row.setOccurredAt(Instant.now());
            row.setHttpMethod("BIZ");
            row.setUriPath(truncate(actionKey, 500));
            row.setDetail(truncate(detailText, 2000));
            row.setResponseStatus(null);
            row.setLogin(SecurityUtils.getCurrentUserLogin().orElse("anonymous"));
            appAuditLogRepository.save(row);
        } catch (Exception e) {
            LOG.warn("Business audit log skipped: {}", e.getMessage());
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
