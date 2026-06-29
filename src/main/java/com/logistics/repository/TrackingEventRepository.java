package com.logistics.repository;

import com.logistics.model.TrackingEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrackingEventRepository extends MongoRepository<TrackingEvent, String> {
    
    List<TrackingEvent> findByShipmentIdOrderByTimestampDesc(String shipmentId);
    
    List<TrackingEvent> findByShipmentIdOrderByTimestampAsc(String shipmentId);
    
    List<TrackingEvent> findByShipmentIdAndStatus(String shipmentId, String status);
}
