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
import org.apache.commons.collections4.ListUtils;
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

    private void fixState(List<QueueItem> items) {
        for (QueueItem item : ListUtils.emptyIfNull(items)) {
            DownloadStatus status = item.getStatus();
            if (status == DownloadStatus.SCHEDULED) {
                item.setStatus(DownloadStatus.READY); // Should be safe to turn SCHEDULED into READY as it hadn't started when the app shut down
            }
            if (item.getProgress() < 0) {
                item.setProgress(0);
            }
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
            fixState(items);
            queueItems.addAll(items);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
