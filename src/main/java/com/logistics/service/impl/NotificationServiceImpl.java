package com.logistics.service.impl;

import com.logistics.dto.NotificationRequest;
import com.logistics.model.Notification;
import com.logistics.repository.NotificationRepository;
import com.logistics.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    @Override
    public List<Notification> getCurrentUserNotifications() {
        String userId = getCurrentUserId();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    @Override
    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    @Override
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }
    
    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }
    
    @Override
    public Notification markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        String userId = getCurrentUserId();
        if (!notification.getUserId().equals(userId)) {
            throw new SecurityException("You can only mark your own notifications as read");
        }
        
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
    
    @Override
    public Notification markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
        return notifications.isEmpty() ? null : notifications.get(0);
    }
    
    @Override
    public Notification createNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setMessage(request.getMessage());
        notification.setType(request.getType());
        notification.setRelatedEntityId(request.getRelatedEntityId());
        notification.setRelatedEntityType(request.getRelatedEntityType());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        
        Notification saved = notificationRepository.save(notification);
        log.info("📢 Notification created for user {}: {}", request.getUserId(), request.getMessage());
        return saved;
    }
    
    @Override
    public Notification createSystemNotification(String message) {
        NotificationRequest request = new NotificationRequest();
        request.setUserId("SYSTEM");
        request.setMessage(message);
        request.setType(Notification.NotificationType.SYSTEM);
        return createNotification(request);
    }
    
    @Override
    public Notification createTaskNotification(String userId, String message, String taskId) {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setMessage(message);
        request.setType(Notification.NotificationType.TASK);
        request.setRelatedEntityId(taskId);
        request.setRelatedEntityType("TASK");
        return createNotification(request);
    }
    
    @Override
    public Notification createDeliveryNotification(String userId, String message, String shipmentId) {
        NotificationRequest request = new NotificationRequest();
        request.setUserId(userId);
        request.setMessage(message);
        request.setType(Notification.NotificationType.DELIVERY);
        request.setRelatedEntityId(shipmentId);
        request.setRelatedEntityType("SHIPMENT");
        return createNotification(request);
    }
    
    @Override
    public void sendBulkNotification(List<String> userIds, String message, Notification.NotificationType type) {
        List<Notification> notifications = new ArrayList<>();
        for (String userId : userIds) {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setMessage(message);
            notification.setType(type);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRead(false);
            notifications.add(notification);
        }
        notificationRepository.saveAll(notifications);
        log.info("📢 Bulk notification sent to {} users", userIds.size());
    }
    
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
