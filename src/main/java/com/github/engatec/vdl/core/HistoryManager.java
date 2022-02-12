package com.github.engatec.vdl.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.db.mapper.HistoryMapper;
import com.github.engatec.vdl.model.HistoryItem;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.preference.ConfigRegistry;
import com.github.engatec.vdl.preference.property.misc.HistoryEntriesNumberConfigProperty;

public class HistoryManager extends VdlManager {

    private ConfigRegistry configRegistry;
    private DbManager dbManager;

    @Override
    public void init() {
        ApplicationContext ctx = ApplicationContext.getInstance();
        configRegistry = ctx.getConfigRegistry();
        dbManager = ctx.getManager(DbManager.class);
    }

    public void addToHistory(Downloadable downloadable) {
        if (configRegistry.get(HistoryEntriesNumberConfigProperty.class).getValue() > 0) {
            HistoryItem item = new HistoryItem(downloadable.getTitle(), downloadable.getBaseUrl(), downloadable.getDownloadPath());
            dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.insertHistoryItems(List.of(item)));
        }
    }

    public CompletableFuture<List<HistoryItem>> getHistoryItemsAsync() {
        Integer maxHistoryItems = configRegistry.get(HistoryEntriesNumberConfigProperty.class).getValue();
        if (maxHistoryItems == 0) {
            return CompletableFuture.completedFuture(List.of());
        }

        return dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(maxHistoryItems));
    }

    public void clearHistory() {
        dbManager.doQueryAsync(HistoryMapper.class, HistoryMapper::clearHistory);
    }

    public void stripHistory() {
        Integer maxHistoryEntries = configRegistry.get(HistoryEntriesNumberConfigProperty.class).getValue();
        ExecutorService systemExecutor = ApplicationContext.getInstance().appExecutors().get(AppExecutors.Type.SYSTEM_EXECUTOR);
        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.stripHistory(maxHistoryEntries), systemExecutor);
    }
}
