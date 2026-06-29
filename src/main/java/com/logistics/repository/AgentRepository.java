package com.logistics.repository;

import com.logistics.model.Agent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository  // ← CRITICAL: This makes it a Spring Bean!
public interface AgentRepository extends MongoRepository<Agent, String> {
    
    Optional<Agent> findByUserId(String userId);
    Optional<Agent> findByEmail(String email);
    Optional<Agent> findByPhone(String phone);
    List<Agent> findByActiveTrue();
    List<Agent> findByWarehouseId(String warehouseId);
    
    @Query("{ $or: [ { 'fullName': { $regex: ?0, $options: 'i' } }, { 'userId': { $regex: ?0, $options: 'i' } } ] }")
    List<Agent> search(String searchTerm);
    
    long countByActiveTrue();
    long countByVehicle(String vehicle);
    List<Agent> findByIsAvailableTrueAndActiveTrue();
}
