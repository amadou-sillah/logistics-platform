package com.logistics.adt;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ShipmentQueue<T> {
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    public void enqueue(T item) {
        queue.offer(item);
    }

    public T dequeue() throws InterruptedException {
        return queue.take();
    }

    public int size() {
        return queue.size();
    }
}
