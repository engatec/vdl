package com.github.engatec.vdl.core;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import com.github.engatec.vdl.core.annotation.Order;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.Language;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class ApplicationContext {

    public static final ApplicationContext INSTANCE = new ApplicationContext();

    public static final String APP_DIR = System.getProperty("app.dir");
    public static final Path DATA_PATH = SystemUtils.getUserHome().toPath().resolve(".vdl");
    public static final Path DB_PATH = DATA_PATH.resolve("data.db");

    public static final Map<Class<? extends VdlManager>, VdlManager> MANAGERS_MAP = new HashMap<>();

    private ConfigRegistry configRegistry;
    private ResourceBundle resourceBundle;

    public static void init(Collection<? extends VdlManager> mgrs) {
        mgrs.stream()
                .sorted(Comparator.comparingInt(it -> {
                    var order = it.getClass().getAnnotation(Order.class);
                    return order == null ? Integer.MAX_VALUE : order.value();
                }))
                .forEach(it -> {
                    MANAGERS_MAP.put(it.getClass(), it);
                    it.init();
                });
    }

    @SuppressWarnings("unchecked")
    public static <T extends VdlManager> T getManager(Class<T> managerClass) {
        return (T) MANAGERS_MAP.computeIfAbsent(managerClass, key -> {throw new NoSuchElementException("Manager '" + managerClass.getSimpleName() + "' hasn't been initialized");});
    }

    public void setLanguage(Language language) {
        resourceBundle = ResourceBundle.getBundle("lang", language.getLocale());
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public void setConfigRegistry(ConfigRegistry configRegistry) {
        this.configRegistry = configRegistry;
    }

    public ConfigRegistry getConfigRegistry() {
        return configRegistry;
    }

    public Charset getSystemCharset() {
        String encoding = System.getProperty("sun.jnu.encoding");
        return Charset.isSupported(encoding) ? Charset.forName(encoding) : Charset.defaultCharset();
    }

    public Path getDownloaderPath(Engine engine) {
        return Path.of(StringUtils.defaultString(APP_DIR, StringUtils.EMPTY), engine.resolveFileName());
    }
}
