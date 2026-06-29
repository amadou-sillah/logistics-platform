package com.logistics.repository;

import com.logistics.model.Shipment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends MongoRepository<Shipment, String> {
    
    // Find by tracking number
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    
    // Find shipments assigned to an agent
    List<Shipment> findByAssignedAgentId(String agentId);
    
    // Find shipments assigned to an agent with specific status
    List<Shipment> findByAssignedAgentIdAndStatus(String agentId, String status);
    
    // Find shipments assigned to an agent with multiple statuses
    @Query("{ 'assignedAgentId': ?0, 'status': { $in: ?1 } }")
    List<Shipment> findByAssignedAgentIdAndStatusIn(String agentId, List<String> statuses);
    
    // Find shipments by warehouse
    List<Shipment> findByWarehouseId(String warehouseId);
    
    // Find shipments by status
    List<Shipment> findByStatus(String status);
    
    // Find shipments by customer email
    List<Shipment> findByCustomerEmail(String email);
    
    // Count shipments by agent
    long countByAssignedAgentId(String agentId);
    
    // Count shipments by status
    long countByStatus(String status);
}
