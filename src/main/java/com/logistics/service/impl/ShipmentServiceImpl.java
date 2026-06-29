package com.logistics.service.impl;

import com.logistics.model.Shipment;
import com.logistics.model.TrackingEvent;
import com.logistics.repository.ShipmentRepository;
import com.logistics.repository.TrackingEventRepository;
import com.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentServiceImpl implements ShipmentService {
    
    private final ShipmentRepository shipmentRepository;
    private final TrackingEventRepository trackingEventRepository;
    
    // ============ EXISTING METHODS ============
    
    @Override
    public List<Shipment> findByAgentId(String agentId) {
        return shipmentRepository.findByAssignedAgentId(agentId);
    }
    
    @Override
    public List<Shipment> findByAgentIdAndStatus(String agentId, String status) {
        return shipmentRepository.findByAssignedAgentIdAndStatus(agentId, status);
    }
    
    @Override
    public List<Shipment> findByWarehouseId(String warehouseId) {
        return shipmentRepository.findByWarehouseId(warehouseId);
    }
    
    @Override
    public List<Shipment> findActiveShipmentsForAgent(String agentId) {
        return shipmentRepository.findByAssignedAgentIdAndStatusIn(agentId, 
            List.of("PENDING", "PICKED_UP", "IN_TRANSIT"));
    }
    
    @Override
    public Shipment assignShipmentToAgent(String shipmentId, String agentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        shipment.setAssignedAgentId(agentId);
        shipment.setStatus("ASSIGNED");
        shipment.setUpdatedAt(LocalDateTime.now());
        Shipment updated = shipmentRepository.save(shipment);
        
        log.info("📦 Shipment {} assigned to agent {}", shipmentId, agentId);
        return updated;
    }
    
    @Override
    public Shipment updateShipmentStatus(String shipmentId, String status) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        String oldStatus = shipment.getStatus();
        shipment.setStatus(status);
        shipment.setUpdatedAt(LocalDateTime.now());
        
        if ("DELIVERED".equals(status)) {
            shipment.setDeliveredAt(LocalDateTime.now());
        }
        
        Shipment updated = shipmentRepository.save(shipment);
        
        log.info("📦 Shipment {} status updated: {} → {}", shipmentId, oldStatus, status);
        return updated;
    }
    
    // ============ NEW METHODS ============
    
    @Override
    public List<Shipment> findAll() {
        log.info("📦 Fetching all shipments");
        return shipmentRepository.findAll();
    }
    
    @Override
    public Optional<Shipment> findById(String id) {
        log.info("📦 Fetching shipment by id: {}", id);
        return shipmentRepository.findById(id);
    }
    
    @Override
    public Optional<Shipment> findByTrackingNumber(String trackingNumber) {
        log.info("📦 Fetching shipment by tracking number: {}", trackingNumber);
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }
    
    @Override
    public Shipment createShipment(Shipment shipment) {
        log.info("📦 Creating new shipment: {}", shipment.getTrackingNumber());
        shipment.setCreatedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());
        if (shipment.getStatus() == null) {
            shipment.setStatus("PENDING");
        }
        Shipment saved = shipmentRepository.save(shipment);
        log.info("✅ Shipment created: {}", saved.getTrackingNumber());
        return saved;
    }
    
    @Override
    public void deleteById(String id) {
        log.info("📦 Deleting shipment: {}", id);
        shipmentRepository.deleteById(id);
        log.info("✅ Shipment deleted: {}", id);
    }
    
    // ============ TRACKING METHODS ============
    
    @Override
    public TrackingEvent addTrackingEvent(String shipmentId, String status, String location, 
                                          String description, Double latitude, Double longitude) {
        log.info("📍 Adding tracking event for shipment: {}", shipmentId);
        
        Shipment shipment = shipmentRepository.findById(shipmentId)
            .orElseThrow(() -> new RuntimeException("Shipment not found"));
        
        TrackingEvent event = new TrackingEvent();
        event.setShipmentId(shipmentId);
        event.setStatus(status);
        event.setLocation(location);
        event.setDescription(description);
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        event.setTimestamp(LocalDateTime.now());
        
        TrackingEvent saved = trackingEventRepository.save(event);
        
        shipment.setStatus(status);
        shipment.setUpdatedAt(LocalDateTime.now());
        shipmentRepository.save(shipment);
        
        log.info("✅ Tracking event added for shipment: {}", shipmentId);
        return saved;
    }
    
    @Override
    public List<TrackingEvent> getTrackingEvents(String shipmentId) {
        log.info("📍 Fetching tracking events for shipment: {}", shipmentId);
        return trackingEventRepository.findByShipmentIdOrderByTimestampDesc(shipmentId);
    }
}
