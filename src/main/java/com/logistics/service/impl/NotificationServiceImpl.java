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
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        String userId = getCurrentUserId();

        if (!n.getUserId().equals(userId)) {
            throw new SecurityException("Not allowed");
        }

        n.setRead(true);
        return notificationRepository.save(n);
    }

    @Override
    public void markAllAsRead(String userId) {
        List<Notification> list = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
    }

    @Override
    public Notification createNotification(NotificationRequest request) {

        Notification n = new Notification();
        n.setUserId(request.getUserId());
        n.setMessage(request.getMessage());

        // ✅ FIXED ENUM HANDLING
        n.setType(request.getType());

        n.setRelatedEntityId(request.getRelatedEntityId());
        n.setRelatedEntityType(request.getRelatedEntityType());

        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);

        return notificationRepository.save(n);
    }

    @Override
    public Notification createSystemNotification(String message) {
        Notification n = new Notification();
        n.setUserId("SYSTEM");
        n.setMessage(message);
        n.setType(Notification.NotificationType.SYSTEM);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);

        return notificationRepository.save(n);
    }

    @Override
    public Notification createTaskNotification(String userId, String message, String taskId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        n.setType(Notification.NotificationType.TASK);
        n.setRelatedEntityId(taskId);
        n.setRelatedEntityType("TASK");
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);

        return notificationRepository.save(n);
    }

    @Override
    public Notification createDeliveryNotification(String userId, String message, String shipmentId) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setMessage(message);
        n.setType(Notification.NotificationType.DELIVERY);
        n.setRelatedEntityId(shipmentId);
        n.setRelatedEntityType("SHIPMENT");
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);

        return notificationRepository.save(n);
    }

    @Override
    public void sendBulkNotification(List<String> userIds, String message, Notification.NotificationType type) {

        List<Notification> list = new ArrayList<>();

        for (String userId : userIds) {
            Notification n = new Notification();
            n.setUserId(userId);
            n.setMessage(message);
            n.setType(type);
            n.setCreatedAt(LocalDateTime.now());
            n.setRead(false);
            list.add(n);
        }

        notificationRepository.saveAll(list);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}