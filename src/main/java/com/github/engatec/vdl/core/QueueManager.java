package com.github.engatec.vdl.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueueManager {

    private static final Logger LOGGER = LogManager.getLogger(QueueManager.class);

    public static final QueueManager INSTANCE = new QueueManager();

    private static final String FILENAME = "queue.vdl";

    private final ObservableList<QueueItem> queueItems = FXCollections.observableList(new LinkedList<>());

    private QueueManager() {
    }

    public void addItem(QueueItem item) {
        // Add most recent item rather then allow to have multiple same items in the queue
        queueItems.remove(item);
        queueItems.add(item);
    }

    public ObservableList<QueueItem> getQueueItems() {
        return queueItems;
    }

    public boolean hasItemsInProgress() {
        return queueItems.stream().anyMatch(it -> it.getStatus() == DownloadStatus.IN_PROGRESS);
    }

    public void persistQueue() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(ApplicationContext.CONFIG_PATH.resolve(FILENAME).toFile(), queueItems);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public void restoreQueue() {
        Path queueFilePath = ApplicationContext.CONFIG_PATH.resolve(FILENAME);
        if (!Files.exists(queueFilePath)) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<QueueItem> items = mapper.readValue(queueFilePath.toFile(), new TypeReference<>(){});
            queueItems.addAll(items);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
