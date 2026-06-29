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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    // Get all notifications for current user
    @GetMapping
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications() {
        List<Notification> notifications = notificationService.getCurrentUserNotifications();
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched successfully", notifications));
    }
    
    // Get unread notifications only
    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications() {
        String userId = getCurrentUserId();
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread notifications fetched", notifications));
    }
    
    // Get unread count
    @GetMapping("/unread/count")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount() {
        String userId = getCurrentUserId();
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread count fetched", count));
    }
    
    // Mark single notification as read
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(@PathVariable String id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", notification));
    }
    
    // Mark all notifications as read
    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        String userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
    
    // Admin creates notification for specific user
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Notification>> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationService.createNotification(request);
        return ResponseEntity.ok(ApiResponse.success("Notification created successfully", notification));
    }
    
    // Admin creates system notification
    @PostMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Notification>> createSystemNotification(
            @RequestParam String message) {
        Notification notification = notificationService.createSystemNotification(message);
        return ResponseEntity.ok(ApiResponse.success("System notification created", notification));
    }
    
    // Admin sends bulk notification
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> sendBulkNotification(
            @RequestParam List<String> userIds,
            @RequestParam String message,
            @RequestParam Notification.NotificationType type) {
        notificationService.sendBulkNotification(userIds, message, type);
        return ResponseEntity.ok(ApiResponse.success("Bulk notifications sent", null));
    }
    
    private String getCurrentUserId() {
        return org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();
    }
}
