package com.github.engatec.vdl.core;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppExecutors {

    private static final Logger LOGGER = LogManager.getLogger(AppExecutors.class);

    public static final ExecutorService COMMON_EXECUTOR = Executors.newFixedThreadPool(2);
    public static final ExecutorService SYSTEM_EXECUTOR = Executors.newFixedThreadPool(3);
    public static final ExecutorService QUEUE_EXECUTOR = Executors.newFixedThreadPool(3);

    public static <T> void runTaskAsync(Task<T> task) {
        COMMON_EXECUTOR.submit(task);
    }

    public static void shutdownExecutors() {
        for (ExecutorService executor : List.of(COMMON_EXECUTOR, SYSTEM_EXECUTOR, QUEUE_EXECUTOR)) {
            shutdownExecutor(executor);
        }
    }

    private static void shutdownExecutor(ExecutorService executor) {
        try {
            executor.shutdownNow();
            boolean successfulTermination = executor.awaitTermination(10, TimeUnit.SECONDS);
            if (!successfulTermination) {
                LOGGER.warn("Executor shutdown abruptly after 10 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
