package com.github.engatec.vdl.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class QueueItem {

    private final ObjectProperty<DownloadStatus> status = new SimpleObjectProperty<>(DownloadStatus.READY);
    private String url;

    public QueueItem() {
    }

    public DownloadStatus getStatus() {
        return status.get();
    }

    public ObjectProperty<DownloadStatus> statusProperty() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status.set(status);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
