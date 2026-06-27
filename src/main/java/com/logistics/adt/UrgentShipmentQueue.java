package com.logistics.adt;

import com.logistics.model.Shipment;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class UrgentShipmentQueue {
    private final PriorityBlockingQueue<Shipment> queue = new PriorityBlockingQueue<>(
            10, Comparator.comparing(s -> "EXPRESS".equals(s.getPriority()) ? 1 : 2)
    );

    public void add(Shipment shipment) {
        queue.offer(shipment);
    }

    public Shipment take() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }
}
