package com.github.engatec.vdl.core;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.preference.ConfigRegistry;
import com.github.engatec.vdl.preference.property.general.DownloadThreadsConfigProperty;
import com.github.engatec.vdl.preference.property.general.LanguageConfigProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ApplicationContext {

    private static final Logger LOGGER = LogManager.getLogger(ApplicationContext.class);

    private static volatile ApplicationContext INSTANCE;

    private Path appBinariesDir;
    private Path appDataDir;
    private ConfigRegistry configRegistry;
    private ResourceBundle resourceBundle;
    private AppExecutors appExecutors;

    private final Map<Class<? extends VdlManager>, VdlManager> managersMap = new HashMap<>();

    public static synchronized void create(
            Path appBinariesDir,
            Path appDataDir,
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
        ctx.configRegistry = configRegistry;
        ctx.appExecutors = new AppExecutors(configRegistry.get(DownloadThreadsConfigProperty.class).getValue());

        Language language = Language.getByLocaleCode(configRegistry.get(LanguageConfigProperty.class).getValue());
        ctx.resourceBundle = ResourceBundle.getBundle("lang", language.getLocale());

        managers.forEach(it -> ctx.managersMap.put(it.getClass(), it));

        ApplicationContext.INSTANCE = ctx;
    }

    public void init() {
        managersMap.values().forEach(VdlManager::init);
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

    public Charset getSystemCharset() {
        String encoding = System.getProperty("sun.jnu.encoding");
        return Charset.isSupported(encoding) ? Charset.forName(encoding) : Charset.defaultCharset();
    }

    public Path getDownloaderPath(Engine engine) {
        return appBinariesDir.resolve(engine.resolveFileName());
    }

    public String getAppVersion() {
        return StringUtils.defaultIfBlank(getClass().getPackage().getImplementationVersion(), "unknown");
    }

    public AppExecutors appExecutors() {
        return appExecutors;
    }
}
