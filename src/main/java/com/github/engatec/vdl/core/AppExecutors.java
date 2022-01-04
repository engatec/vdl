package com.github.engatec.vdl.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppExecutors {

    private static final Logger LOGGER = LogManager.getLogger(AppExecutors.class);

    public enum Type {
        COMMON_EXECUTOR, SYSTEM_EXECUTOR, QUEUE_EXECUTOR
    }

    private final Map<Type, ExecutorService> executorsMap = new HashMap<>();

    public AppExecutors(int queueExecutorThreads) {
        executorsMap.put(Type.COMMON_EXECUTOR, Executors.newFixedThreadPool(5));
        executorsMap.put(Type.SYSTEM_EXECUTOR, Executors.newFixedThreadPool(1));
        executorsMap.put(Type.QUEUE_EXECUTOR, Executors.newFixedThreadPool(queueExecutorThreads));
    }

    public ExecutorService get(Type type) {
        return executorsMap.get(type);
    }

    public void shutdown() {
        executorsMap.entrySet().stream()
                .filter(it -> it.getKey() != Type.SYSTEM_EXECUTOR)
                .map(Map.Entry::getValue)
                .forEach(ExecutorService::shutdownNow);

        // Let's try to shutdown SYSTEM_EXECUTOR gracefully as it is intended for an important work
        try {
            ExecutorService systemExecutor = executorsMap.get(Type.SYSTEM_EXECUTOR);
            systemExecutor.shutdown();
            int awaitTimeout = 10;
            boolean successfulTermination = systemExecutor.awaitTermination(awaitTimeout, TimeUnit.SECONDS);
            if (!successfulTermination) {
                LOGGER.warn("systemExecutor wasn't able to finish its job in {} seconds", awaitTimeout);
                systemExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
