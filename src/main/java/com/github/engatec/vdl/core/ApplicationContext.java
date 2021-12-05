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
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationContext {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationContext.class);

    private static volatile ApplicationContext INSTANCE;

    private Path appBinariesDir;
    private Path appDataDir;
    private String dbName;
    private ConfigRegistry configRegistry;
    private ResourceBundle resourceBundle;
    public Map<Class<? extends VdlManager>, VdlManager> managersMap;

    public static synchronized void init(
            Path appBinariesDir,
            Path appDataDir,
            String dbName,
            ConfigRegistry configRegistry,
            Collection<? extends VdlManager> managers
    ) {
        if (INSTANCE != null) {
            LOGGER.warn("Context has already been initialized");
            return;
        }

        var ctx = new ApplicationContext();
        ctx.appBinariesDir = appBinariesDir;
        ctx.appDataDir = appDataDir;
        ctx.dbName = dbName;
        ctx.configRegistry = configRegistry;

        Language language = Language.getByLocaleCode(configRegistry.get(LanguagePref.class).getValue());
        ctx.resourceBundle = ResourceBundle.getBundle("lang", language.getLocale());

        ctx.managersMap = new HashMap<>();
        managers.stream()
                .sorted(Comparator.comparingInt(it -> {
                    var order = it.getClass().getAnnotation(Order.class);
                    return order == null ? Integer.MAX_VALUE : order.value();
                }))
                .forEach(it -> {
                    ctx.managersMap.put(it.getClass(), it);
                    it.init(ctx);
                });

        ApplicationContext.INSTANCE = ctx;
    }

    public static ApplicationContext getInstance() {
        if (INSTANCE == null) {
            LOGGER.fatal("Context hasn't been initialized");
            System.exit(-1);
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T extends VdlManager> T getManager(Class<T> managerClass) {
        return (T) managersMap.computeIfAbsent(managerClass, key -> {throw new NoSuchElementException("Manager '" + managerClass.getSimpleName() + "' hasn't been initialized");});
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public String getLocalizedString(String key) {
        return resourceBundle.getString(key);
    }

    public String getLocalizedString(String key, Language language) {
        return ResourceBundle.getBundle("lang", language.getLocale()).getString(key);
    }

    public ConfigRegistry getConfigRegistry() {
        return configRegistry;
    }

    public Path getAppBinariesDir() {
        return appBinariesDir;
    }

    public Path getAppDataDir() {
        return appDataDir;
    }

    public String getDbName() {
        return dbName;
    }

    public Charset getSystemCharset() {
        String encoding = System.getProperty("sun.jnu.encoding");
        return Charset.isSupported(encoding) ? Charset.forName(encoding) : Charset.defaultCharset();
    }

    public Path getDownloaderPath(Engine engine) {
        return appBinariesDir.resolve(engine.resolveFileName());
    }
}
