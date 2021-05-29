package com.github.engatec.vdl.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.downloadable.BaseDownloadable;
import com.github.engatec.vdl.model.preferences.general.AutoSelectFormatConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSelectFormatPref;
import com.github.engatec.vdl.util.YouDlUtils;
import com.github.engatec.vdl.worker.service.PlaylistDetailsSearchService;
import javafx.application.Platform;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubscriptionsManager {

    private static final Logger LOGGER = LogManager.getLogger(SubscriptionsManager.class);

    public static final SubscriptionsManager INSTANCE = new SubscriptionsManager();

    private static final String FILENAME = "subscriptions.vdl";

    private final List<Subscription> subscriptions = new CopyOnWriteArrayList<>();

    private SubscriptionsManager() {
    }

    public List<Subscription> getSubscriptions() {
        return Collections.unmodifiableList(subscriptions);
    }

    public void subscribe(Subscription s) {
        subscriptions.add(s);
    }

    public void unsubscribe(Subscription s) {
        subscriptions.remove(s);
    }

    public void persist() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(ApplicationContext.CONFIG_PATH.resolve(FILENAME).toFile(), subscriptions);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public void restore() {
        Path path = ApplicationContext.CONFIG_PATH.resolve(FILENAME);
        if (!Files.exists(path)) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Subscription> items = mapper.readValue(path.toFile(), new TypeReference<>(){});
            subscriptions.addAll(items);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public void checkForUpdates(Subscription subscription) {
        var playlistSearchService = new PlaylistDetailsSearchService(AppExecutors.SYSTEM_EXECUTOR);
        playlistSearchService.setUrl(subscription.getUrl());
        playlistSearchService.setOnSucceeded(e -> updateSubscription(subscription, (List<VideoInfo>) e.getSource().getValue()));
        playlistSearchService.setOnFailed(e -> LOGGER.error(e.getSource().getException().getMessage()));
        playlistSearchService.start();
    }

    public void massCheckForUpdates() {
        for (Subscription subscription : subscriptions) {
            checkForUpdates(subscription);
        }
    }

    private void updateSubscription(Subscription subscription, List<VideoInfo> playlistItems) {
        if (CollectionUtils.isEmpty(playlistItems)) {
            LOGGER.warn("No videos found for subscription '{}'", subscription.getName());
            return;
        }

        Set<String> processedItems = subscription.getProcessedItems();
        List<VideoInfo> newItems = playlistItems.stream()
                .filter(Predicate.not(it -> processedItems.contains(getPlaylistItemId(it))))
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

            Platform.runLater(() -> QueueManager.INSTANCE.addItem(new QueueItem(downloadable)));

            processedItems.add(getPlaylistItemId(vi));
        }
    }

    public String getPlaylistItemId(VideoInfo item) {
        return StringUtils.firstNonBlank(item.getId(), item.getUrl(), item.getTitle());
    }
}
