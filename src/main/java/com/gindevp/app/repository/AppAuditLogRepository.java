package com.gindevp.app.repository;

import com.gindevp.app.domain.AppAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppAuditLogRepository extends JpaRepository<AppAuditLog, Long> {
    Page<AppAuditLog> findAllByOrderByOccurredAtDesc(Pageable pageable);
}
