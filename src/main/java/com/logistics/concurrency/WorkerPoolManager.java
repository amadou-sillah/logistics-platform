package com.logistics.concurrency;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class WorkerPoolManager {

    @Value("${app.concurrency.core-pool-size:10}")
    private int corePoolSize;

    @Value("${app.concurrency.max-pool-size:50}")
    private int maxPoolSize;

    @Value("${app.concurrency.queue-capacity:1000}")
    private int queueCapacity;

    private BlockingQueue<Runnable> taskQueue;
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        taskQueue = new ArrayBlockingQueue<>(queueCapacity);
        executor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize,
                60L, TimeUnit.SECONDS,
                taskQueue,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        log.info("WorkerPool initialized with core={}, max={}", corePoolSize, maxPoolSize);
    }

    public void submit(Runnable task) {
        executor.submit(task);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down WorkerPool...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
