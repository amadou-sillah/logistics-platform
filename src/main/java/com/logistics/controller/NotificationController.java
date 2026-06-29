package com.logistics.controller;

import com.logistics.dto.ApiResponse;
import com.logistics.dto.NotificationRequest;
import com.logistics.model.Notification;
import com.logistics.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    // =========================
    // GET CURRENT USER NOTIFICATIONS
    // =========================
    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications() {

        String userId = getCurrentUserId();
        List<Notification> notifications = notificationService.getCurrentUserNotifications();

        return ResponseEntity.ok(
                ApiResponse.success("Notifications fetched successfully", notifications)
        );
    }

    // =========================
    // UNREAD NOTIFICATIONS
    // =========================
    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications() {

        String userId = getCurrentUserId();
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);

        return ResponseEntity.ok(
                ApiResponse.success("Unread notifications fetched", notifications)
        );
    }

    // =========================
    // UNREAD COUNT
    // =========================
    @GetMapping("/unread/count")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {

        String userId = getCurrentUserId();
        long count = notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(
                ApiResponse.success("Unread count fetched", count)
        );
    }

    // =========================
    // MARK ONE AS READ
    // =========================
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(@PathVariable String id) {

        Notification notification = notificationService.markAsRead(id);

        return ResponseEntity.ok(
                ApiResponse.success("Notification marked as read", notification)
        );
    }

    // =========================
    // MARK ALL AS READ
    // =========================
    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {

        String userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(
                ApiResponse.success("All notifications marked as read", null)
        );
    }

    // =========================
    // CREATE NOTIFICATION (ADMIN)
    // =========================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Notification>> createNotification(
            @Valid @RequestBody NotificationRequest request) {

        Notification notification = notificationService.createNotification(request);

        return ResponseEntity.ok(
                ApiResponse.success("Notification created successfully", notification)
        );
    }

    // =========================
    // SYSTEM NOTIFICATION
    // =========================
    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Notification>> createSystemNotification(
            @RequestParam String message) {

        Notification notification = notificationService.createSystemNotification(message);

        return ResponseEntity.ok(
                ApiResponse.success("System notification created", notification)
        );
    }

    // =========================
    // BULK NOTIFICATION
    // =========================
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> sendBulkNotification(
            @RequestParam List<String> userIds,
            @RequestParam String message,
            @RequestParam Notification.NotificationType type) {

        notificationService.sendBulkNotification(userIds, message, type);

        return ResponseEntity.ok(
                ApiResponse.success("Bulk notifications sent", null)
        );
    }

    // =========================
    // OPTIONAL: ADMIN DEBUG ENDPOINT (VERY USEFUL)
    // =========================
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Notification>>> getAllNotifications() {

        List<Notification> notifications = notificationService.getAllNotifications();

        return ResponseEntity.ok(
                ApiResponse.success("All notifications fetched", notifications)
        );
    }

    // =========================
    // SAFE USER ID EXTRACTION
    // =========================
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("User not authenticated");
        }

        return auth.getName();
    }
}