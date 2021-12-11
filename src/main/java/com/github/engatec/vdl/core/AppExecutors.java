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

    public static final ExecutorService COMMON_EXECUTOR = Executors.newFixedThreadPool(5);
    public static final ExecutorService SYSTEM_EXECUTOR = Executors.newFixedThreadPool(1);
    public static final ExecutorService QUEUE_EXECUTOR = Executors.newFixedThreadPool(3);

    public static <T> void runTaskAsync(Task<T> task) {
        COMMON_EXECUTOR.submit(task);
    }

    public static void shutdownExecutors() {
        for (ExecutorService executor : List.of(COMMON_EXECUTOR, QUEUE_EXECUTOR)) {
            executor.shutdownNow();
        }

        // Let's try to shutdown SYSTEM_EXECUTOR gracefully as it is intended for an important work
        try {
            SYSTEM_EXECUTOR.shutdown();
            int awaitTimeout = 10;
            boolean successfulTermination = SYSTEM_EXECUTOR.awaitTermination(awaitTimeout, TimeUnit.SECONDS);
            if (!successfulTermination) {
                LOGGER.warn("SYSTEM_EXECUTOR wasn't able to finish its job in {} seconds", awaitTimeout);
                SYSTEM_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
