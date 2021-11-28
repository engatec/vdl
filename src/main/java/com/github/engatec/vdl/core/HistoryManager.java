package com.github.engatec.vdl.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.db.mapper.HistoryMapper;
import com.github.engatec.vdl.model.HistoryItem;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.preferences.wrapper.misc.HistoryEntriesNumberPref;
import com.github.engatec.vdl.util.AppUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HistoryManager extends VdlManager {

    private static final Logger LOGGER = LogManager.getLogger(HistoryManager.class);

    public static final HistoryManager INSTANCE = new HistoryManager();
    private final ConfigRegistry configRegistry = ApplicationContext.INSTANCE.getConfigRegistry();

    private DbManager dbManager;

    @Override
    public void init() {
        dbManager = ApplicationContext.INSTANCE.getManager(DbManager.class);

        // FIXME: deprecated in 1.7 For removal in 1.9
        CompletableFuture.supplyAsync(this::restoreFromJson, AppExecutors.COMMON_EXECUTOR)
                .thenAccept(items -> {
                    if (CollectionUtils.isNotEmpty(items)) {
                        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.insertHistoryItems(items));
                    }
                });
    }

    public void addToHistory(Downloadable downloadable) {
        if (configRegistry.get(HistoryEntriesNumberPref.class).getValue() > 0) {
            HistoryItem item = new HistoryItem(downloadable.getTitle(), downloadable.getBaseUrl(), downloadable.getDownloadPath());
            dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.insertHistoryItems(List.of(item)));
        }
    }

    public CompletableFuture<List<HistoryItem>> getHistoryItemsAsync() {
        Integer maxHistoryItems = configRegistry.get(HistoryEntriesNumberPref.class).getValue();
        if (maxHistoryItems == 0) {
            return CompletableFuture.completedFuture(List.of());
        }

        return dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(maxHistoryItems));
    }

    // FIXME: transition from JSON files to sqlite.
    @Deprecated(since = "1.7", forRemoval = true)
    public List<HistoryItem> restoreFromJson() {
        Path historyFilePath = ApplicationContext.DATA_PATH.resolve("history.vdl");
        if (Files.notExists(historyFilePath)) {
            return List.of();
        }

        List<HistoryItem> result = List.of();
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(historyFilePath.toFile(), new TypeReference<>(){});
            for (HistoryItem it : result) {
                ZonedDateTime dtm = LocalDateTime.parse(it.getCreatedAt(), AppUtils.DATE_TIME_FORMATTER).atZone(ZoneId.systemDefault());
                it.setCreatedAt(dtm.withZoneSameInstant(ZoneId.of("GMT")).format(AppUtils.DATE_TIME_FORMATTER_SQLITE));
            }
            Files.delete(historyFilePath);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        }
        return result;
    }

    public void clearHistory() {
        dbManager.doQueryAsync(HistoryMapper.class, HistoryMapper::clearHistory);
    }

    public void stripHistory() {
        Integer maxHistoryEntries = configRegistry.get(HistoryEntriesNumberPref.class).getValue();
        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.stripHistory(maxHistoryEntries), AppExecutors.SYSTEM_EXECUTOR);
    }
}
