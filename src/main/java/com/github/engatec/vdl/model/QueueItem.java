package com.github.engatec.vdl.model;

import java.nio.file.Path;

import com.github.engatec.vdl.model.downloadable.Downloadable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class QueueItem implements Downloadable {

    private final ObjectProperty<DownloadStatus> status = new SimpleObjectProperty<>(DownloadStatus.READY);
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final StringProperty size = new SimpleStringProperty();
    private final StringProperty throughput = new SimpleStringProperty();

    private String url;
    private Path downloadPath;
    private String formatId;

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

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public String getSize() {
        return size.get();
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public String getThroughput() {
        return throughput.get();
    }

    public StringProperty throughputProperty() {
        return throughput;
    }

    public void setThroughput(String throughput) {
        this.throughput.set(throughput);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Path getDownloadPath() {
        return downloadPath;
    }

    @Override
    public void setDownloadPath(Path downloadPath) {
        this.downloadPath = downloadPath;
    }

    @Override
    public String getFormatId() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    @Override
    public String getBaseUrl() {
        return url;
    }
}
