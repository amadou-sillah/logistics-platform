package com.logistics.service;

import com.logistics.model.Shipment;
import com.logistics.model.TrackingEvent;

import java.util.List;
import java.util.Optional;

public interface ShipmentService {

    List<Shipment> findByAgentId(String agentId);
    List<Shipment> findByAgentIdAndStatus(String agentId, String status);
    List<Shipment> findByWarehouseId(String warehouseId);
    List<Shipment> findActiveShipmentsForAgent(String agentId);

    Shipment assignShipmentToAgent(String shipmentId, String agentId);
    Shipment updateShipmentStatus(String shipmentId, String status);

    List<Shipment> findAll();
    Optional<Shipment> findById(String id);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    Shipment createShipment(Shipment shipment);
    void deleteById(String id);

    TrackingEvent addTrackingEvent(
            String shipmentId,
            String eventType,
            String description,
            String location,
            Double latitude,
            Double longitude
    );

    List<TrackingEvent> getTrackingEvents(String shipmentId);
}