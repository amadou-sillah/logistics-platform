package com.logistics.pattern.builder;

import com.logistics.model.Shipment;
import java.time.LocalDateTime;

public class ShipmentBuilder {
    private String trackingNumber;
    private String customerId;
    private String origin;
    private String destination;
    private String status;
    private LocalDateTime eta;
    private Double amount;
    private String priority;

    public ShipmentBuilder trackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
        return this;
    }
    public ShipmentBuilder customerId(String customerId) {
        this.customerId = customerId;
        return this;
    }
    public ShipmentBuilder origin(String origin) {
        this.origin = origin;
        return this;
    }
    public ShipmentBuilder destination(String destination) {
        this.destination = destination;
        return this;
    }
    public ShipmentBuilder status(String status) {
        this.status = status;
        return this;
    }
    public ShipmentBuilder eta(LocalDateTime eta) {
        this.eta = eta;
        return this;
    }
    public ShipmentBuilder amount(Double amount) {
        this.amount = amount;
        return this;
    }
    public ShipmentBuilder priority(String priority) {
        this.priority = priority;
        return this;
    }

    public Shipment build() {
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber(trackingNumber);
        shipment.setCustomerId(customerId);
        shipment.setOrigin(origin);
        shipment.setDestination(destination);
        shipment.setStatus(status);
        shipment.setEta(eta);
        shipment.setAmount(amount);
        shipment.setPriority(priority);
        return shipment;
    }
}
