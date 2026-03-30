package com.gindevp.app.web.rest;

import com.gindevp.app.domain.AppAuditLog;
import com.gindevp.app.security.AuthoritiesConstants;
import com.gindevp.app.service.AppAuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api/app-audit-logs")
public class AppAuditLogResource {

    private final AppAuditLogService appAuditLogService;

    public AppAuditLogResource(AppAuditLogService appAuditLogService) {
        this.appAuditLogService = appAuditLogService;
    }

    @GetMapping("")
    @PreAuthorize(
        "hasAnyAuthority('" +
        AuthoritiesConstants.ADMIN +
        "', '" +
        AuthoritiesConstants.ASSET_MANAGER +
        "', '" +
        AuthoritiesConstants.GD +
        "')"
    )
    public ResponseEntity<java.util.List<AppAuditLog>> getAll(Pageable pageable) {
        Page<AppAuditLog> page = appAuditLogService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
