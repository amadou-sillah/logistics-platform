package com.logistics.service;

import com.logistics.model.Shipment;
import java.util.List;

public interface ShipmentService {
    
    // FIXED: Agent-specific queries instead of findAll()
    List<Shipment> findByAgentId(String agentId);
    
    List<Shipment> findByAgentIdAndStatus(String agentId, String status);
    
    List<Shipment> findByWarehouseId(String warehouseId);
    
    List<Shipment> findActiveShipmentsForAgent(String agentId);
    
    Shipment assignShipmentToAgent(String shipmentId, String agentId);
    
    Shipment updateShipmentStatus(String shipmentId, String status);
}
