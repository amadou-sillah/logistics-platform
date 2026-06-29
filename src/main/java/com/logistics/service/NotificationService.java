package com.logistics.service;

import com.logistics.dto.NotificationRequest;
import com.logistics.model.Notification;
import java.util.List;

public interface NotificationService {
    
    List<Notification> getCurrentUserNotifications();
    
    List<Notification> getUserNotifications(String userId);
    
    List<Notification> getUnreadNotifications(String userId);
    
    long getUnreadCount(String userId);
    
    Notification markAsRead(String notificationId);
    
    Notification markAllAsRead(String userId);
    
    Notification createNotification(NotificationRequest request);
    
    Notification createSystemNotification(String message);
    
    Notification createTaskNotification(String userId, String message, String taskId);
    
    Notification createDeliveryNotification(String userId, String message, String shipmentId);
    
    void sendBulkNotification(List<String> userIds, String message, Notification.NotificationType type);
}
