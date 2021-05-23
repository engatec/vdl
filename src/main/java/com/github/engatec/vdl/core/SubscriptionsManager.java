package com.github.engatec.vdl.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.model.Subscription;
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
}
