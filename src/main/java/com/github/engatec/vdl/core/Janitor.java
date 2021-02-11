package com.github.engatec.vdl.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.github.engatec.vdl.core.preferences.ConfigManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Janitor {

    private static final Logger LOGGER = LogManager.getLogger(Janitor.class);

    public static void cleanUpPropetiesFile() {
        try {
            File propertiesFile = ApplicationContext.CONFIG_PATH.resolve("config.properties").toFile();
            if (!propertiesFile.exists()) {
                return;
            }

            var props = new Properties();
            try (var fr = new FileReader(propertiesFile)) {
                props.load(fr);
            } catch (IOException e) {
                // Окей, не нашли файл настроек. Не страшно, используем значения по умолчанию :)
            }

            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                if (StringUtils.isNotBlank(value)) {
                    ConfigManager.INSTANCE.setValue(key, value);
                }
            }

            boolean propsDeleted = propertiesFile.delete();
            if (!propsDeleted) {
                LOGGER.warn("Couldn't delete obsolete properties file");
            }
        } catch (Exception ex) {
            // Вот никому нахрен не нужно, чтобы при уборке приложуха свалилась. Поэтому поймаем все, отпишемся в лог, и забудем.
            LOGGER.warn(ex.getMessage(), ex);
        }
    }
}
