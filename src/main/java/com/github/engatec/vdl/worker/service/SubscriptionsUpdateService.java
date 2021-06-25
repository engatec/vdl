package com.github.engatec.vdl.worker.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.HistoryManager;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.downloadable.BaseDownloadable;
import com.github.engatec.vdl.model.preferences.general.AutoSelectFormatConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSelectFormatPref;
import com.github.engatec.vdl.util.YouDlUtils;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubscriptionsUpdateService extends Service<Void> {

    private static final Logger LOGGER = LogManager.getLogger(SubscriptionsUpdateService.class);

    private final CountDownLatch updatesCountDownLatch;
    private final List<Subscription> subscriptions;

    public SubscriptionsUpdateService(List<Subscription> subscriptions) {
        super();
        setExecutor(AppExecutors.COMMON_EXECUTOR);
        this.subscriptions = subscriptions;
        this.updatesCountDownLatch = new CountDownLatch(subscriptions.size());
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                for (Subscription subscription : subscriptions) {
                    if (Thread.interrupted()) {
                        cancel();
                    }

                    if (isCancelled()) {
                        return null;
                    }

                    var playlistSearchService = new PlaylistDetailsSearchService(AppExecutors.SYSTEM_EXECUTOR);
                    playlistSearchService.setUrl(subscription.getUrl());
                    playlistSearchService.setOnSucceeded(e -> updateSubscription(subscription, (List<VideoInfo>) e.getSource().getValue()));
                    playlistSearchService.setOnFailed(e -> LOGGER.error(e.getSource().getException().getMessage()));
                    playlistSearchService.runningProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue) {
                            updatesCountDownLatch.countDown();
                        }
                    });
                    playlistSearchService.start();
                }

                updatesCountDownLatch.await(); // Subscriptions are updated in different threads, wait for all of them to finish
                return null;
            }

            private void updateSubscription(Subscription subscription, List<VideoInfo> playlistItems) {
                if (CollectionUtils.isEmpty(playlistItems)) {
                    LOGGER.warn("No videos found for subscription '{}'", subscription.getName());
                    return;
                }

                Set<String> processedItems = subscription.getProcessedItems();
                List<VideoInfo> newItems = playlistItems.stream()
                        .filter(Predicate.not(it -> processedItems.contains(SubscriptionsManager.INSTANCE.buildPlaylistItemId(it))))
                        .collect(Collectors.toList());

                Integer selectedVideoHeight = ConfigRegistry.get(AutoSelectFormatPref.class).getValue();
                selectedVideoHeight = AutoSelectFormatConfigItem.DEFAULT.equals(selectedVideoHeight) ? null : selectedVideoHeight;

                for (VideoInfo vi : newItems) {
                    String formatId = YouDlUtils.createFormatByHeight(selectedVideoHeight);

                    var downloadable = new BaseDownloadable();
                    downloadable.setBaseUrl(vi.getBaseUrl());
                    downloadable.setTitle(vi.getTitle());
                    downloadable.setFormatId(formatId);
                    downloadable.setDownloadPath(Path.of(subscription.getPath()));

                    Platform.runLater(() -> {
                        HistoryManager.INSTANCE.addToHistory(downloadable);
                        QueueManager.INSTANCE.addItem(new QueueItem(downloadable));
                    });

                    processedItems.add(SubscriptionsManager.INSTANCE.buildPlaylistItemId(vi));
                }
            }
        };
    }
}
