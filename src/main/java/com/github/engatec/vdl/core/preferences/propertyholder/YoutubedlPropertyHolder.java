package com.github.engatec.vdl.core.preferences.propertyholder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class YoutubedlPropertyHolder {

    private final BooleanProperty noMTime = new SimpleBooleanProperty();

    public boolean isNoMTime() {
        return noMTime.get();
    }

    public BooleanProperty noMTimeProperty() {
        return noMTime;
    }

    public void setNoMTime(boolean noMTime) {
        this.noMTime.set(noMTime);
    }
}
