package com.github.engatec.vdl.core.preferences.propertyholder;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GeneralPropertyHolder {

    private final BooleanProperty alwaysAskPath = new SimpleBooleanProperty();
    private final StringProperty downloadPath = new SimpleStringProperty();

    private final BooleanProperty autoSearchFromClipboard = new SimpleBooleanProperty();

    private final BooleanProperty autoDownload = new SimpleBooleanProperty();
    private final BooleanProperty autodownloadUseCustomSettings = new SimpleBooleanProperty();
    private final StringProperty autodownloadCustomSettings = new SimpleStringProperty();

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

    public boolean isAutoSearchFromClipboard() {
        return autoSearchFromClipboard.get();
    }

    public BooleanProperty autoSearchFromClipboardProperty() {
        return autoSearchFromClipboard;
    }

    public void setAutoSearchFromClipboard(boolean autoSearchFromClipboard) {
        this.autoSearchFromClipboard.set(autoSearchFromClipboard);
    }

    public boolean isAutoDownload() {
        return autoDownload.get();
    }

    public BooleanProperty autoDownloadProperty() {
        return autoDownload;
    }

    public void setAutoDownload(boolean autoDownload) {
        this.autoDownload.set(autoDownload);
    }

    public boolean isAutodownloadUseCustomSettings() {
        return autodownloadUseCustomSettings.get();
    }

    public BooleanProperty autodownloadUseCustomSettingsProperty() {
        return autodownloadUseCustomSettings;
    }

    public void setAutodownloadUseCustomSettings(boolean autodownloadUseCustomSettings) {
        this.autodownloadUseCustomSettings.set(autodownloadUseCustomSettings);
    }

    public String getAutodownloadCustomSettings() {
        return autodownloadCustomSettings.get();
    }

    public StringProperty autodownloadCustomSettingsProperty() {
        return autodownloadCustomSettings;
    }

    public void setAutodownloadCustomSettings(String autodownloadCustomSettings) {
        this.autodownloadCustomSettings.set(autodownloadCustomSettings);
    }
}
