package com.github.engatec.vdl.core;

import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.engatec.vdl.model.Language;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationContext {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationContext.class);

    public static final ApplicationContext INSTANCE = new ApplicationContext();

    public static final String APP_DIR = System.getProperty("app.dir");

    public static final Path CONFIG_PATH = SystemUtils.getUserHome().toPath().resolve(".vdl");

    private final ObjectProperty<ResourceBundle> resourceBundleProperty = new SimpleObjectProperty<>();

    private final ExecutorService sharedExecutor;
    private final ExecutorService queueExecutor;

    public ApplicationContext() {
        sharedExecutor = Executors.newFixedThreadPool(2);
        queueExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    public void setLanguage(Language language) {
        resourceBundleProperty.setValue(ResourceBundle.getBundle("lang", language.getLocale()));
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundleProperty.get();
    }

    public ObjectProperty<ResourceBundle> getResourceBundleProperty() {
        return resourceBundleProperty;
    }

    public String getSystemEncoding() {
        return StringUtils.defaultIfBlank(System.getProperty("sun.jnu.encoding"), System.getProperty("file.encoding"));
    }

    public <T> void runTaskAsync(Task<T> task) {
        sharedExecutor.submit(task);
    }

    public ExecutorService getSharedExecutor() {
        return sharedExecutor;
    }

    public ExecutorService getQueueExecutor() {
        return queueExecutor;
    }

    public Path getYoutubeDlPath() {
        return Path.of(StringUtils.defaultString(APP_DIR, StringUtils.EMPTY), resolveYoutubeDlFileName());
    }

    private String resolveYoutubeDlFileName() {
        return StringUtils.defaultIfBlank(
                System.getProperty("app.youtubedl"),
                SystemUtils.IS_OS_WINDOWS ? "youtube-dl.exe" : "youtube-dl" // If app.youtubedl is not set, assume default name
        );
    }

    public void shutdownExecutors() {
        for (ExecutorService executor : List.of(sharedExecutor, queueExecutor)) {
            shutdownExecutor(executor);
        }
    }

    private void shutdownExecutor(ExecutorService executor) {
        try {
            executor.shutdownNow();
            boolean successfulTermination = executor.awaitTermination(10, TimeUnit.SECONDS);
            if (!successfulTermination) {
                LOGGER.warn("Executor shutdown abruptly after 10 seconds");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
