package com.github.engatec.vdl.core.preferences.propertyholder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class YoutubedlPropertyHolder {

    private final BooleanProperty noMTime = new SimpleBooleanProperty();

    private final BooleanProperty useConfigFile = new SimpleBooleanProperty();
    private final StringProperty configFilePath = new SimpleStringProperty();

    public boolean isNoMTime() {
        return noMTime.get();
    }

    public BooleanProperty noMTimeProperty() {
        return noMTime;
    }

    public void setNoMTime(boolean noMTime) {
        this.noMTime.set(noMTime);
    }

    public boolean isUseConfigFile() {
        return useConfigFile.get();
    }

    public BooleanProperty useConfigFileProperty() {
        return useConfigFile;
    }

    public void setUseConfigFile(boolean useConfigFile) {
        this.useConfigFile.set(useConfigFile);
    }

    public String getConfigFilePath() {
        return configFilePath.get();
    }

    public StringProperty configFilePathProperty() {
        return configFilePath;
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath.set(configFilePath);
    }
}
