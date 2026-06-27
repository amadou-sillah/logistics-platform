package com.logistics.repository;

import com.logistics.model.Shipment;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends BaseRepository<Shipment, String> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    List<Shipment> findByCustomerId(String customerId);
    List<Shipment> findByStatus(String status);
}
