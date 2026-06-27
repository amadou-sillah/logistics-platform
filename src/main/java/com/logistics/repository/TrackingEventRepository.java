package com.logistics.repository;

import com.logistics.model.TrackingEvent;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TrackingEventRepository extends BaseRepository<TrackingEvent, String> {
    List<TrackingEvent> findByShipmentIdOrderByOccurredAtDesc(String shipmentId);
}
