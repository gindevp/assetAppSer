package com.gindevp.app.web.rest;

import com.gindevp.app.domain.AppNotification;
import com.gindevp.app.service.AppNotificationService;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app-notifications")
public class AppNotificationResource {

    private final AppNotificationService appNotificationService;

    public AppNotificationResource(AppNotificationService appNotificationService) {
        this.appNotificationService = appNotificationService;
    }

    @GetMapping("")
    public ResponseEntity<List<AppNotification>> getMine() {
        return ResponseEntity.ok(appNotificationService.getMyNotifications());
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(appNotificationService.getMyUnreadCount());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable("id") Long id) {
        appNotificationService.markRead(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllRead() {
        appNotificationService.markAllRead();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/push")
    public ResponseEntity<AppNotification> pushMine(@RequestBody PushNotificationRequest request) {
        AppNotification saved = appNotificationService.pushForCurrentUser(
            request.title(),
            request.message(),
            request.kind(),
            request.route()
        );
        return ResponseEntity.ok(saved);
    }

    public record PushNotificationRequest(
        @NotBlank String title,
        @NotBlank String message,
        String kind,
        String route
    ) {}
}
