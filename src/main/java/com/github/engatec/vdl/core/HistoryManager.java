package com.github.engatec.vdl.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.HistoryItem;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.preferences.wrapper.misc.HistoryEntriesNumberPref;
import com.github.engatec.vdl.util.AppUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HistoryManager {

    private static final Logger LOGGER = LogManager.getLogger(HistoryManager.class);

    private final Object lock = new Object();

    public static final HistoryManager INSTANCE = new HistoryManager();
    private final ConfigRegistry configRegistry = ApplicationContext.INSTANCE.getConfigRegistry();

    private static final String FILENAME = "history.vdl";

    private CircularFifoQueue<HistoryItem> historyQueue = new CircularFifoQueue<>();

    private HistoryManager() {
    }

    public synchronized void addToHistory(Downloadable downloadable) {
        if (configRegistry.get(HistoryEntriesNumberPref.class).getValue() > 0) {
            HistoryItem historyItem = new HistoryItem(downloadable.getTitle(), downloadable.getBaseUrl(), downloadable.getDownloadPath(), AppUtils.convertDtmToString(LocalDateTime.now()));
            synchronized (lock) {
                historyQueue.offer(historyItem);
            }
        }
    }

    public List<HistoryItem> getHistoryItems() {
        synchronized (lock) {
            return List.copyOf(historyQueue);
        }
    }

    public void reviseHistorySize() {
        Integer historyEntriesMaxNumber = configRegistry.get(HistoryEntriesNumberPref.class).getValue();
        if (historyEntriesMaxNumber <= 0) {
            synchronized (lock) {
                historyQueue.clear();
            }
            return;
        }

        if (historyQueue.maxSize() != historyEntriesMaxNumber) {
            synchronized (lock) {
                CircularFifoQueue<HistoryItem> newQueue = new CircularFifoQueue<>(historyEntriesMaxNumber);
                newQueue.addAll(historyQueue);
                historyQueue = newQueue;
            }
        }
    }

    public void persist() {
        flushToDisk(getHistoryItems());
    }

    public void restore() {
        Integer historyEntriesMaxNumber = configRegistry.get(HistoryEntriesNumberPref.class).getValue();
        if (historyEntriesMaxNumber <= 0) {
            synchronized (lock) {
                historyQueue.clear();
            }
            return;
        }

        List<HistoryItem> loadedItems = new ArrayList<>(historyEntriesMaxNumber);
        Path historyPath = ApplicationContext.CONFIG_PATH.resolve(FILENAME);
        if (Files.exists(historyPath)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                loadedItems.addAll(mapper.readValue(historyPath.toFile(), new TypeReference<>(){}));
            } catch (IOException e) {
                LOGGER.warn(e.getMessage());
            }
        }

        synchronized (lock) {
            CircularFifoQueue<HistoryItem> newQueue = new CircularFifoQueue<>(historyEntriesMaxNumber);
            newQueue.addAll(loadedItems);
            newQueue.addAll(historyQueue);
            historyQueue = newQueue;
        }
    }

    public void clearHistory() {
        synchronized (lock) {
            historyQueue.clear();
        }
        flushToDisk(List.of());
    }

    private void flushToDisk(List<HistoryItem> historyItems) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(ApplicationContext.CONFIG_PATH.resolve(FILENAME).toFile(), historyItems);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
    }
}
