package com.github.engatec.vdl.core;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ResourceBundle;

import com.github.engatec.vdl.model.Language;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class ApplicationContext {

    public static final ApplicationContext INSTANCE = new ApplicationContext();

    public static final String APP_DIR = System.getProperty("app.dir");

    public static final Path CONFIG_PATH = SystemUtils.getUserHome().toPath().resolve(".vdl");

    private ResourceBundle resourceBundle;

    public void setLanguage(Language language) {
        resourceBundle = ResourceBundle.getBundle("lang", language.getLocale());
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public Charset getSystemCharset() {
        String encoding = System.getProperty("sun.jnu.encoding");
        return Charset.isSupported(encoding) ? Charset.forName(encoding) : Charset.defaultCharset();
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
}
