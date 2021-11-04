package com.github.engatec.vdl.core;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ResourceBundle;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.Language;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class ApplicationContext {

    public static final ApplicationContext INSTANCE = new ApplicationContext();

    public static final String APP_DIR = System.getProperty("app.dir");

    public static final Path CONFIG_PATH = SystemUtils.getUserHome().toPath().resolve(".vdl");

    private ConfigRegistry configRegistry;
    private ResourceBundle resourceBundle;

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

    public Path getYoutubeDlPath() {
        return Path.of(StringUtils.defaultString(APP_DIR, StringUtils.EMPTY), Downloader.YOUTUBE_DL.resolveFileName());
    }

    public Path getYtDlpPath() {
        return Path.of(StringUtils.defaultString(APP_DIR, StringUtils.EMPTY), Downloader.YT_DLP.resolveFileName());
    }
}
