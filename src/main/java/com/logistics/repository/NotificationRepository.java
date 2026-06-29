package com.logistics.repository;

import com.logistics.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    // Find all notifications for a user, sorted by newest first
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    // Find unread notifications for a user
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(String userId);
    
    // Count unread notifications
    long countByUserIdAndReadFalse(String userId);
    
    // Mark all as read for a user
    @Query("{ 'userId': ?0 }")
    void markAllAsRead(String userId);
}
