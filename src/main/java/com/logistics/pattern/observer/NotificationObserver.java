package com.logistics.pattern.observer;

import com.logistics.model.Notification;
import com.logistics.model.User;
import com.logistics.repository.NotificationRepository;
import com.logistics.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationObserver {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @EventListener
    public void handleShipmentStatusChange(ShipmentStatusEvent event) {
        String customerId = event.getShipment().getCustomerId();
        if (customerId == null) return;
        User customer = userRepository.findById(customerId).orElse(null);
        if (customer == null) return;
        Notification notification = new Notification();
        notification.setUserId(customer.getId());
        notification.setTitle("Shipment Status Updated");
        notification.setMessage(String.format("Shipment %s status changed from %s to %s",
                event.getShipment().getTrackingNumber(),
                event.getOldStatus(), event.getNewStatus()));
        notification.setRead(false);
        notificationRepository.save(notification);
    }
}
