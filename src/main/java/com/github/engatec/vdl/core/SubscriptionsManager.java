package com.github.engatec.vdl.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.worker.service.SubscriptionsUpdateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubscriptionsManager {

    private static final Logger LOGGER = LogManager.getLogger(SubscriptionsManager.class);

    public static final SubscriptionsManager INSTANCE = new SubscriptionsManager();

    private static final String FILENAME = "subscriptions.vdl";

    private final List<Subscription> subscriptions = new CopyOnWriteArrayList<>();

    private Consumer<Boolean> subscriptionsUpdateProgressListener;

    private SubscriptionsManager() {
    }

    public List<Subscription> getSubscriptions() {
        return List.copyOf(subscriptions);
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
            mapper.writeValue(ApplicationContext.DATA_PATH.resolve(FILENAME).toFile(), subscriptions);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public void restore() {
        Path path = ApplicationContext.DATA_PATH.resolve(FILENAME);
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

    public void updateSubscription(Subscription subscription) {
        doSubscriptionsUpdate(List.of(subscription));
    }

    public void updateAllSubscriptions() {
        doSubscriptionsUpdate(List.copyOf(subscriptions));
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
