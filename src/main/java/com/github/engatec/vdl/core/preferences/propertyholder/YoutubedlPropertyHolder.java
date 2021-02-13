package com.github.engatec.vdl.core.preferences.propertyholder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class YoutubedlPropertyHolder {

    private final BooleanProperty noMTime = new SimpleBooleanProperty();

    private final BooleanProperty useCustomArguments = new SimpleBooleanProperty();
    private final StringProperty customArguments = new SimpleStringProperty();

    public boolean isNoMTime() {
        return noMTime.get();
    }

    public BooleanProperty noMTimeProperty() {
        return noMTime;
    }

    public void setNoMTime(boolean noMTime) {
        this.noMTime.set(noMTime);
    }

    public boolean isUseCustomArguments() {
        return useCustomArguments.get();
    }

    public BooleanProperty useCustomArgumentsProperty() {
        return useCustomArguments;
    }

    public void setUseCustomArguments(boolean useCustomArguments) {
        this.useCustomArguments.set(useCustomArguments);
    }

    public String getCustomArguments() {
        return customArguments.get();
    }

    public StringProperty customArgumentsProperty() {
        return customArguments;
    }

    public void setCustomArguments(String customArguments) {
        this.customArguments.set(customArguments);
    }
}
