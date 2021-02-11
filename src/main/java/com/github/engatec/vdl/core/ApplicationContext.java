package com.github.engatec.vdl.core;

import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.engatec.vdl.model.Language;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class ApplicationContext {

    public static final ApplicationContext INSTANCE = new ApplicationContext();

    public static final String APP_DIR = System.getProperty("app.dir");
    public static final String YOUTUBE_DL_APP_NAME = "youtube-dl";

    public static final Path CONFIG_PATH = SystemUtils.getUserHome().toPath().resolve(".vdl");

    ExecutorService executorService;
    private final ObjectProperty<ResourceBundle> resourceBundleProperty = new SimpleObjectProperty<>();

    public ApplicationContext() {
        executorService = Executors.newSingleThreadExecutor();
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
        executorService.submit(task);
    }

    public void cleanUp() {
        executorService.shutdownNow();
    }
}
