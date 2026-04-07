package com.gindevp.app.web.rest;

import com.gindevp.app.service.AppAuditLogService;
import com.gindevp.app.service.RequestRealtimeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditLoggingInterceptor implements HandlerInterceptor {

    private final AppAuditLogService appAuditLogService;
    private final RequestRealtimeService requestRealtimeService;

    public AuditLoggingInterceptor(AppAuditLogService appAuditLogService, RequestRealtimeService requestRealtimeService) {
        this.appAuditLogService = appAuditLogService;
        this.requestRealtimeService = requestRealtimeService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String method = request.getMethod();
        if ("GET".equals(method) || "OPTIONS".equals(method) || "HEAD".equals(method)) {
            return;
        }
        String uri = request.getRequestURI();
        if (uri == null || !uri.startsWith("/api/")) {
            return;
        }
        if (uri.startsWith("/api/authenticate")) {
            return;
        }
        int status = response.getStatus();
        if (status >= 400) {
            return;
        }
        appAuditLogService.record(method, uri, status);
        requestRealtimeService.publishHttpMutation(method, uri, status);
    }
}
