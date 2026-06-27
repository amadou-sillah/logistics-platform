package com.logistics.service;

import com.logistics.model.Shipment;
import com.logistics.model.TrackingEvent;
import com.logistics.repository.ShipmentRepository;
import com.logistics.repository.TrackingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final TrackingEventRepository trackingEventRepository;

    public Shipment createShipment(Shipment shipment) {
        shipment.setTrackingNumber(generateTrackingNumber());
        shipment.setStatus("PENDING");
        shipment.setCreatedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Cacheable(value = "shipments", key = "#trackingNumber")
    public Optional<Shipment> findByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }

    public List<Shipment> findByCustomerId(String customerId) {
        return shipmentRepository.findByCustomerId(customerId);
    }

    public Shipment updateStatus(String shipmentId, String newStatus) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        shipment.setStatus(newStatus);
        shipment.setUpdatedAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }

    public TrackingEvent addTrackingEvent(String shipmentId, String eventType, String description,
                                          String location, Double lat, Double lng) {
        TrackingEvent event = new TrackingEvent();
        event.setShipmentId(shipmentId);
        event.setEventType(eventType);
        event.setDescription(description);
        event.setLocation(location);
        event.setLatitude(lat);
        event.setLongitude(lng);
        event.setOccurredAt(LocalDateTime.now());
        return trackingEventRepository.save(event);
    }

    public List<TrackingEvent> getTrackingEvents(String shipmentId) {
        return trackingEventRepository.findByShipmentIdOrderByOccurredAtDesc(shipmentId);
    }

    public List<Shipment> findAll() {
        return shipmentRepository.findAll();
    }

    public Optional<Shipment> findById(String id) {
        return shipmentRepository.findById(id);
    }

    public void deleteById(String id) {
        shipmentRepository.deleteById(id);
    }
}
