package com.github.engatec.vdl.core;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.HistoryItem;
import com.github.engatec.vdl.model.preferences.wrapper.misc.HistoryEntriesNumberPref;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HistoryManager {

    private static final Logger LOGGER = LogManager.getLogger(HistoryManager.class);

    public static final HistoryManager INSTANCE = new HistoryManager();

    private static final String FILENAME = "history.vdl";

    private final Queue<HistoryItem> newItems = new LinkedList<>();

    private HistoryManager() {
    }

    public void addHistoryItem(HistoryItem item) {
        Integer historyEntriesMaxNumber = ConfigRegistry.get(HistoryEntriesNumberPref.class).getValue();
        while (newItems.size() >= historyEntriesMaxNumber) {
            newItems.poll();
        }
        newItems.offer(item);
    }

    public List<HistoryItem> getHistoryItems() {
        Path historyPath = ApplicationContext.CONFIG_PATH.resolve(FILENAME);
        if (Files.notExists(historyPath)) {
            return List.copyOf(newItems);
        }

        Integer historyEntriesMaxNumber = ConfigRegistry.get(HistoryEntriesNumberPref.class).getValue();
        if (historyEntriesMaxNumber == 0) {
            return List.of();
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<HistoryItem> historyItems = mapper.readValue(historyPath.toFile(), new TypeReference<>(){});
            historyItems.addAll(newItems);
            return historyItems.stream()
                    .skip(Math.max(0, historyItems.size() - historyEntriesMaxNumber))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
            throw new UncheckedIOException(e);
        }
    }

    public synchronized void persistHistory() {
        flushToDisk(getHistoryItems());
        newItems.clear();
    }

    public synchronized void clearHistory() {
        flushToDisk(List.of());
        newItems.clear();
    }

    private synchronized void flushToDisk(List<HistoryItem> historyItems) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(ApplicationContext.CONFIG_PATH.resolve(FILENAME).toFile(), historyItems);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
