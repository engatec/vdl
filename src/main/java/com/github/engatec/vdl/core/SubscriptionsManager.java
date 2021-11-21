package com.github.engatec.vdl.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.db.mapper.SubscriptionMapper;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.worker.service.SubscriptionsUpdateService;
import javafx.application.Platform;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubscriptionsManager extends VdlManager {

    private static final Logger LOGGER = LogManager.getLogger(SubscriptionsManager.class);

    private DbManager dbManager;

    public static final SubscriptionsManager INSTANCE = new SubscriptionsManager();

    private Consumer<Boolean> subscriptionsUpdateProgressListener;

    @Override
    public void init() {
        dbManager = ApplicationContext.getManager(DbManager.class);

        // FIXME: deprecated in 1.7 For removal in 1.9
        CompletableFuture.supplyAsync(this::restoreFromJson, AppExecutors.COMMON_EXECUTOR)
                .thenAccept(items -> {
                    for (Subscription it : ListUtils.emptyIfNull(items)) {
                        subscribe(it, it.getProcessedItemsForTraversal());
                    }
                }).join();
    }

    public void subscribe(Subscription s, Set<String> processedItems) {
        dbManager.doQueryAsync(SubscriptionMapper.class, mapper -> {
            mapper.insertSubscription(s);
            if (CollectionUtils.isNotEmpty(processedItems)) {
                mapper.insertProcessedItems(s.getId(), processedItems);
            }
            return 0;
        });
    }

    public void unsubscribe(Subscription s) {
        dbManager.doQueryAsync(SubscriptionMapper.class, mapper -> mapper.deleteSubscription(s.getId()));
    }

    public CompletableFuture<List<Subscription>> getSubscriptionsAsync() {
        return dbManager.doQueryAsync(SubscriptionMapper.class, SubscriptionMapper::fetchSubscriptions);
    }

    public void addProcessedItems(Subscription s, Set<String> processedItems) {
        if (CollectionUtils.isEmpty(processedItems)) {
            return;
        }

        try {
            dbManager.doQueryAsync(SubscriptionMapper.class, mapper -> mapper.insertProcessedItems(s.getId(), processedItems))
                    .thenRun(() -> s.getProcessedItems().addAll(processedItems));
        } catch (PersistenceException e) { // In case subscription has been removed before processed items insertion
            LOGGER.warn("Couldn't add processed items for subscription '{}'. Id '{}' doesn't exist.", s.getName(), s.getId());
        }
    }

    // FIXME: transition from JSON files to sqlite.
    @Deprecated(since = "1.7", forRemoval = true)
    public List<Subscription> restoreFromJson() {
        Path subscriptionFilePath = ApplicationContext.DATA_PATH.resolve("subscriptions.vdl");
        if (Files.notExists(subscriptionFilePath)) {
            return List.of();
        }

        List<Subscription> result = List.of();
        ObjectMapper mapper = new ObjectMapper();
        try {
            result = mapper.readValue(subscriptionFilePath.toFile(), new TypeReference<>(){});
            Files.delete(subscriptionFilePath);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return result;
    }

    public void updateSubscription(Subscription subscription) {
        doSubscriptionsUpdate(List.of(subscription));
    }

    public void updateAllSubscriptions() {
        getSubscriptionsAsync().thenAccept(subscriptions -> Platform.runLater(() -> doSubscriptionsUpdate(subscriptions)));
    }

    private void doSubscriptionsUpdate(List<Subscription> subscriptions) {
        if (CollectionUtils.isEmpty(subscriptions)) {
            return;
        }

        var subscriptionsUpdateService = new SubscriptionsUpdateService(subscriptions);
        subscriptionsUpdateService.runningProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && subscriptionsUpdateProgressListener != null) {
                subscriptionsUpdateProgressListener.accept(newValue);
            }
        });
        subscriptionsUpdateService.start();
    }

    public String buildPlaylistItemId(VideoInfo item) {
        return StringUtils.firstNonBlank(item.getId(), item.getUrl(), item.getTitle());
    }

    public void setSubscriptionsUpdateProgressListener(Consumer<Boolean> subscriptionsUpdateProgressListener) {
        this.subscriptionsUpdateProgressListener = subscriptionsUpdateProgressListener;
    }
}
