package com.logistics.pattern.factory;

import com.logistics.model.Notification;
import com.logistics.model.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {
    public Notification createEmailNotification(User user, String title, String message) {
        Notification n = new Notification();
        n.setUserId(user.getId());
        n.setTitle("[Email] " + title);
        n.setMessage(message);
        return n;
    }

    public Notification createSmsNotification(User user, String title, String message) {
        Notification n = new Notification();
        n.setUserId(user.getId());
        n.setTitle("[SMS] " + title);
        n.setMessage(message);
        return n;
    }

    public Notification createPushNotification(User user, String title, String message) {
        Notification n = new Notification();
        n.setUserId(user.getId());
        n.setTitle("[Push] " + title);
        n.setMessage(message);
        return n;
    }
}
