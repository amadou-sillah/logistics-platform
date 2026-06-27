package com.logistics.cache;

import com.logistics.model.Shipment;
import com.logistics.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShipmentCacheService {
    private final ShipmentRepository shipmentRepository;

    @Cacheable(value = "shipments", key = "#trackingNumber")
    public Shipment getByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber).orElse(null);
    }

    @CachePut(value = "shipments", key = "#shipment.trackingNumber")
    public Shipment updateCache(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    @CacheEvict(value = "shipments", key = "#trackingNumber")
    public void evict(String trackingNumber) {}
}
