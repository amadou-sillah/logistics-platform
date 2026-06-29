package com.logistics.repository;

import com.logistics.model.Warehouse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends MongoRepository<Warehouse, String> {
    
    Optional<Warehouse> findByName(String name);
    Optional<Warehouse> findByCode(String code);
    List<Warehouse> findByIsActiveTrue();
    List<Warehouse> findByLocationContainingIgnoreCase(String location);
    long countByIsActiveTrue();
}
