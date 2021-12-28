package com.github.engatec.vdl.service;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.HistoryManager;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.downloadable.BaseDownloadable;
import com.github.engatec.vdl.model.preferences.general.AutoSelectFormatConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSelectFormatPref;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.util.YouDlUtils;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubscriptionsUpdateService extends Service<Void> {

    private static final Logger LOGGER = LogManager.getLogger(SubscriptionsUpdateService.class);

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final QueueManager queueManager = ctx.getManager(QueueManager.class);
    private final HistoryManager historyManager = ctx.getManager(HistoryManager.class);
    private final SubscriptionsManager subscriptionsManager = ctx.getManager(SubscriptionsManager.class);

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
                Set<PlaylistDetailsSearchService> runningServices = new HashSet<>();

                for (Subscription subscription : subscriptions) {
                    if (Thread.interrupted()) {
                        cancel();
                    }

                    if (isCancelled()) {
                        return null;
                    }

                    var playlistSearchService = new PlaylistDetailsSearchService();
                    playlistSearchService.setUrl(subscription.getUrl());
                    playlistSearchService.setOnSucceeded(e -> updateSubscription(subscription, (List<VideoInfo>) e.getSource().getValue()));
                    playlistSearchService.setOnFailed(e -> {
                        String msg = e.getSource().getException().getMessage();
                        LOGGER.warn(msg);
                        Platform.runLater(() -> Dialogs.exception("subscriptions.playlist.update.error", msg));
                    });
                    playlistSearchService.runningProperty().addListener((observable, oldValue, newValue) -> {
                        if (!newValue) {
                            runningServices.remove(playlistSearchService);
                            updatesCountDownLatch.countDown();
                        }
                    });
                    runningServices.add(playlistSearchService);
                    playlistSearchService.start();
                }

                int latchWaitingTime = 3;
                boolean countDownFinished = updatesCountDownLatch.await(latchWaitingTime, TimeUnit.MINUTES); // Subscriptions are updated in different threads, wait for all of them to finish
                if (!countDownFinished) {
                    LOGGER.warn("Couldn't update all subscriptions in {} minutes. {} playlists left to process.", latchWaitingTime, runningServices.size());
                }

                return null;
            }

            private void updateSubscription(Subscription subscription, List<VideoInfo> playlistItems) {
                if (CollectionUtils.isEmpty(playlistItems)) {
                    LOGGER.warn("No videos found for subscription '{}'", subscription.getName());
                    return;
                }

                Set<String> processedItems = subscription.getProcessedItemsForTraversal();
                List<VideoInfo> newItems = playlistItems.stream()
                        .filter(Predicate.not(it -> processedItems.contains(subscriptionsManager.buildPlaylistItemId(it))))
                        .toList();

                Integer selectedVideoHeight = ctx.getConfigRegistry().get(AutoSelectFormatPref.class).getValue();
                selectedVideoHeight = AutoSelectFormatConfigItem.DEFAULT.equals(selectedVideoHeight) ? null : selectedVideoHeight;

                for (VideoInfo vi : newItems) {
                    String formatId = YouDlUtils.createFormatByHeight(selectedVideoHeight);

                    var downloadable = new BaseDownloadable();
                    downloadable.setBaseUrl(vi.getBaseUrl());
                    downloadable.setTitle(vi.getTitle());
                    downloadable.setFormatId(formatId);
                    downloadable.setDownloadPath(Path.of(subscription.getDownloadPath()));

                    Platform.runLater(() -> {
                        historyManager.addToHistory(downloadable);
                        queueManager.addItem(new QueueItem(downloadable));
                    });

                }

                subscriptionsManager.addProcessedItems(subscription, newItems.stream().map(subscriptionsManager::buildPlaylistItemId).collect(Collectors.toSet()));
            }
        };
    }
}
