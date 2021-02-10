package com.github.engatec.vdl.core;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

public class ConfigManager {

    public static final ConfigManager INSTANCE = new ConfigManager();

    private static final String CONFIG_FILENAME = "config.properties";

    private Properties properties;

    private ConfigManager() {
    }

    public void saveConfig() {
        Path servicePath = ApplicationContext.CONFIG_PATH;
        try {
            Files.createDirectories(servicePath);
            try (var fw = new FileWriter(servicePath.resolve(CONFIG_FILENAME).toFile())) {
                properties.store(fw, null);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void loadConfig() {
        properties = new Properties();
        try (var fr = new FileReader(ApplicationContext.CONFIG_PATH.resolve(CONFIG_FILENAME).toFile())) {
            properties.load(fr);
        } catch (IOException e) {
            // Окей, не нашли файл настроек. Не страшно, используем значения по умолчанию :)
        }
    }

    public String getValue(ConfigProperty prop) {
        return Objects.requireNonNullElse(properties.getProperty(prop.getKey()), prop.getDefaultValue());
    }

    public void setValue(ConfigProperty prop, String value) {
        properties.setProperty(prop.getKey(), value);
    }
}
