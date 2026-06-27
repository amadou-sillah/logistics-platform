package com.logistics.controller;

import com.logistics.dto.TrackingEventDto;
import com.logistics.model.TrackingEvent;
import com.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
public class TrackingController {
    private final ShipmentService shipmentService;

    @PostMapping("/event")
    public TrackingEvent addEvent(@RequestBody TrackingEventDto dto) {
        return shipmentService.addTrackingEvent(
                dto.getShipmentId(),
                dto.getEventType(),
                dto.getDescription(),
                dto.getLocation(),
                dto.getLatitude(),
                dto.getLongitude()
        );
    }

    @GetMapping("/{shipmentId}")
    public List<TrackingEvent> getEvents(@PathVariable String shipmentId) {
        return shipmentService.getTrackingEvents(shipmentId);
    }
}
