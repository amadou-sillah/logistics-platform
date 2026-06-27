package com.logistics.pattern.observer;

import com.logistics.model.Shipment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ShipmentStatusEvent extends ApplicationEvent {
    private final Shipment shipment;
    private final String oldStatus;
    private final String newStatus;

    public ShipmentStatusEvent(Object source, Shipment shipment, String oldStatus, String newStatus) {
        super(source);
        this.shipment = shipment;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }
}
