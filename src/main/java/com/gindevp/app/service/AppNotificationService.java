package com.gindevp.app.service;

import com.gindevp.app.domain.AppNotification;
import com.gindevp.app.domain.User;
import com.gindevp.app.repository.AppNotificationRepository;
import com.gindevp.app.repository.UserRepository;
import com.gindevp.app.security.SecurityUtils;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@Transactional
public class AppNotificationService {

    private final AppNotificationRepository appNotificationRepository;
    private final UserRepository userRepository;
    private final RequestRealtimeService requestRealtimeService;

    public AppNotificationService(
        AppNotificationRepository appNotificationRepository,
        UserRepository userRepository,
        RequestRealtimeService requestRealtimeService
    ) {
        this.appNotificationRepository = appNotificationRepository;
        this.userRepository = userRepository;
        this.requestRealtimeService = requestRealtimeService;
    }

    @Transactional(readOnly = true)
    public List<AppNotification> getMyNotifications() {
        String login = SecurityUtils.getCurrentUserLogin().orElse("anonymous");
        return appNotificationRepository.findTop100ByRecipientLoginOrderByCreatedAtDesc(login);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getMyUnreadCount() {
        String login = SecurityUtils.getCurrentUserLogin().orElse("anonymous");
        long unread = appNotificationRepository.countByRecipientLoginAndIsReadFalse(login);
        return Map.of("unread", unread);
    }

    public void markRead(Long id) {
        String login = SecurityUtils.getCurrentUserLogin().orElse("anonymous");
        appNotificationRepository.findByIdAndRecipientLogin(id, login).ifPresent(n -> {
            if (!Boolean.TRUE.equals(n.getIsRead())) {
                n.setIsRead(true);
                appNotificationRepository.save(n);
                publishRealtimeAfterCommit();
            }
        });
    }

    public void markAllRead() {
        String login = SecurityUtils.getCurrentUserLogin().orElse("anonymous");
        List<AppNotification> unreadRows = appNotificationRepository.findByRecipientLoginAndIsReadFalse(login);
        if (unreadRows.isEmpty()) return;
        for (AppNotification n : unreadRows) {
            n.setIsRead(true);
        }
        appNotificationRepository.saveAll(unreadRows);
        publishRealtimeAfterCommit();
    }

    public AppNotification pushForCurrentUser(String title, String message, String kind, String route) {
        String login = SecurityUtils.getCurrentUserLogin().orElse("anonymous");
        AppNotification saved = pushForLogin(login, title, message, kind, route);
        publishRealtimeAfterCommit();
        return saved;
    }

    public AppNotification pushForEmployeeId(Long employeeId, String title, String message, String kind, String route) {
        User user = userRepository.findOneByEmployee_Id(employeeId).orElse(null);
        if (user == null || user.getLogin() == null || user.getLogin().isBlank()) {
            return null;
        }
        AppNotification saved = pushForLogin(user.getLogin(), title, message, kind, route);
        publishRealtimeAfterCommit();
        return saved;
    }

    public void pushForAuthorities(String title, String message, String kind, String route, String... authorities) {
        if (authorities == null || authorities.length == 0) return;
        List<User> users = userRepository.findDistinctByActivatedIsTrueAndAuthorities_NameIn(Arrays.asList(authorities));
        if (users.isEmpty()) return;
        Set<String> logins = users
            .stream()
            .map(User::getLogin)
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.toSet());
        if (logins.isEmpty()) return;
        for (String login : logins) {
            pushForLogin(login, title, message, kind, route);
        }
        publishRealtimeAfterCommit();
    }

    private AppNotification pushForLogin(String login, String title, String message, String kind, String route) {
        AppNotification n = new AppNotification();
        n.setRecipientLogin(login);
        n.setTitle(truncate(title, 200));
        n.setMessage(truncate(message, 2000));
        n.setKind(normalizeKind(kind));
        n.setRoute(truncate(route, 500));
        n.setIsRead(false);
        n.setCreatedAt(Instant.now());
        return appNotificationRepository.save(n);
    }

    private void publishRealtimeAfterCommit() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            requestRealtimeService.publishNotificationChange();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                requestRealtimeService.publishNotificationChange();
            }
        });
    }

    private static String normalizeKind(String kind) {
        String k = kind == null ? "info" : kind.trim().toLowerCase();
        return switch (k) {
            case "success", "warning", "error" -> k;
            default -> "info";
        };
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
