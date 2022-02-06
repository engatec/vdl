package com.github.engatec.vdl.core;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.db.mapper.SubscriptionMapper;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.service.SubscriptionsUpdateService;
import javafx.application.Platform;
import org.apache.commons.collections4.CollectionUtils;
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
