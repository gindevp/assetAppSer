package com.gindevp.app.repository;

import com.gindevp.app.domain.AppNotification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findTop100ByRecipientLoginOrderByCreatedAtDesc(String recipientLogin);

    long countByRecipientLoginAndIsReadFalse(String recipientLogin);

    Optional<AppNotification> findByIdAndRecipientLogin(Long id, String recipientLogin);

    List<AppNotification> findByRecipientLoginAndIsReadFalse(String recipientLogin);
}
