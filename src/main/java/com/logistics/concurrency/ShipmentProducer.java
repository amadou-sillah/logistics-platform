package com.logistics.concurrency;

import com.logistics.model.Shipment;
import com.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShipmentProducer {
    private final WorkerPoolManager workerPool;
    private final ShipmentService shipmentService;

    public void processShipmentAsync(Shipment shipment) {
        workerPool.submit(() -> {
            log.info("Processing shipment {}", shipment.getTrackingNumber());
            // Simulate heavy processing
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            shipmentService.save(shipment);
            log.info("Shipment {} processed", shipment.getTrackingNumber());
        });
    }
}
