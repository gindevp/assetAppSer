package com.gindevp.app.config;

import com.gindevp.app.web.rest.AuditLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuditWebConfiguration implements WebMvcConfigurer {

    private final AuditLoggingInterceptor auditLoggingInterceptor;

    public AuditWebConfiguration(AuditLoggingInterceptor auditLoggingInterceptor) {
        this.auditLoggingInterceptor = auditLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditLoggingInterceptor).addPathPatterns("/api/**");
    }
}
