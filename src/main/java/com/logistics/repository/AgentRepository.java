package com.logistics.repository;

import com.logistics.model.Agent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends MongoRepository<Agent, String> {
    
    // Find by userId (unique)
    Optional<Agent> findByUserId(String userId);
    
    // Find by email (unique)
    Optional<Agent> findByEmail(String email);
    
    // Find by phone
    Optional<Agent> findByPhone(String phone);
    
    // Find active agents
    List<Agent> findByActiveTrue();
    
    // Find agents by warehouse
    List<Agent> findByWarehouseId(String warehouseId);
    
    // Search agents by name or userId
    @Query("{ $or: [ { 'fullName': { $regex: ?0, $options: 'i' } }, { 'userId': { $regex: ?0, $options: 'i' } } ] }")
    List<Agent> search(String searchTerm);
    
    // Count active agents
    long countByActiveTrue();
    
    // Count agents by vehicle type
    long countByVehicle(String vehicle);
    
    // Find available agents
    List<Agent> findByIsAvailableTrueAndActiveTrue();
}
