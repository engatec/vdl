package com.github.engatec.vdl.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import com.github.engatec.vdl.service.SubscriptionsUpdateService;
import com.github.engatec.vdl.util.AppUtils;
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

    private Consumer<Boolean> subscriptionsUpdateProgressListener;

    @Override
    public void init() {
        ApplicationContext ctx = ApplicationContext.getInstance();
        dbManager = ctx.getManager(DbManager.class);

        // FIXME: deprecated in 1.7 For removal in 1.9
        restoreFromJson(ctx.getAppDataDir().resolve("subscriptions.vdl"));
    }

    public void subscribe(Subscription s) {
        dbManager.doQuery(SubscriptionMapper.class, mapper -> {
            mapper.insertSubscription(s);
            Set<String> processedItems = s.getProcessedItemsForTraversal();
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

        s.getProcessedItems().addAll(processedItems);
        try {
            dbManager.doQueryAsync(SubscriptionMapper.class, mapper -> mapper.insertProcessedItems(s.getId(), processedItems));
        } catch (PersistenceException e) { // In case subscription has been removed before processed items insertion
            LOGGER.warn("Couldn't add processed items for subscription '{}'. Id '{}' doesn't exist.", s.getName(), s.getId());
        }
    }

    public void update(Subscription s) {
        dbManager.doQueryAsync(SubscriptionMapper.class, mapper -> mapper.updateSubscription(s));
    }

    // FIXME: transition from JSON files to sqlite.
    @Deprecated(since = "1.7", forRemoval = true)
    public void restoreFromJson(Path subscriptionFilePath) {
        if (Files.notExists(subscriptionFilePath)) {
            return;
        }

        List<Subscription> items = List.of();
        ObjectMapper mapper = new ObjectMapper();
        try {
            items = mapper.readValue(subscriptionFilePath.toFile(), new TypeReference<>(){});
            Files.delete(subscriptionFilePath);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        for (Subscription s : ListUtils.emptyIfNull(items)) {
            ZonedDateTime dtm = LocalDateTime.parse(s.getCreatedAt(), AppUtils.DATE_TIME_FORMATTER).atZone(ZoneId.systemDefault());
            s.setCreatedAt(dtm.withZoneSameInstant(ZoneId.of("GMT")).format(AppUtils.DATE_TIME_FORMATTER_SQLITE));
            subscribe(s);
        }
    }

    public void refresh(Subscription subscription) {
        doRefresh(List.of(subscription));
    }

    public void refreshAll() {
        getSubscriptionsAsync().thenAccept(subscriptions -> Platform.runLater(() -> doRefresh(subscriptions)));
    }

    private void doRefresh(List<Subscription> subscriptions) {
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
