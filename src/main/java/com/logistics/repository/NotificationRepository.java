package com.logistics.repository;

import com.logistics.model.Notification;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends BaseRepository<Notification, String> {
}
