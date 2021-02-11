package com.github.engatec.vdl.core.preferences.propertyholder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GeneralPropertyHolder {

    private final BooleanProperty alwaysAskPath = new SimpleBooleanProperty();
    private final StringProperty downloadPath = new SimpleStringProperty();

    public boolean isAlwaysAskPath() {
        return alwaysAskPath.get();
    }

    public BooleanProperty alwaysAskPathProperty() {
        return alwaysAskPath;
    }

    public void setAlwaysAskPath(boolean alwaysAskPath) {
        this.alwaysAskPath.set(alwaysAskPath);
    }

    public String getDownloadPath() {
        return downloadPath.get();
    }

    public StringProperty downloadPathProperty() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath.set(downloadPath);
    }
}
