package com.github.engatec.vdl.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.engatec.vdl.model.downloadable.BaseDownloadable;
import com.github.engatec.vdl.model.downloadable.CustomFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.collections4.ListUtils;

public class QueueItem implements Downloadable {

    private final ObjectProperty<DownloadStatus> status = new SimpleObjectProperty<>(DownloadStatus.READY);
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final StringProperty size = new SimpleStringProperty();
    private final StringProperty throughput = new SimpleStringProperty();

    @JsonIgnore
    private final BaseDownloadable downloadable;

    public QueueItem() {
        downloadable = new CustomFormatDownloadable(null, null);
    }

    public QueueItem(BaseDownloadable downloadable) {
        this.downloadable = downloadable;
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

    @Override
    public Path getDownloadPath() {
        return downloadable.getDownloadPath();
    }

    @Override
    public void setDownloadPath(Path downloadPath) {
        downloadable.setDownloadPath(downloadPath);
    }

    @Override
    public String getFormatId() {
        return downloadable.getFormatId();
    }

    public void setFormatId(String formatId) {
        if (downloadable instanceof CustomFormatDownloadable) {
            ((CustomFormatDownloadable) downloadable).setFormatId(formatId);
        }
    }

    @Override
    public String getTitle() {
        return downloadable.getTitle();
    }

    public void setTitle(String title) {
        downloadable.setTitle(title);
    }

    @Override
    public String getBaseUrl() {
        return downloadable.getBaseUrl();
    }

    public void setBaseUrl(String baseUrl) {
        downloadable.setBaseUrl(baseUrl);
    }

    @Override
    public List<Postprocessing> getPostprocessingSteps() {
        return downloadable.getPostprocessingSteps();
    }

    @Override
    public void setPostprocessingSteps(List<Postprocessing> items) {
        downloadable.setPostprocessingSteps(List.copyOf(ListUtils.emptyIfNull(items)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueueItem queueItem = (QueueItem) o;
        return getBaseUrl().equals(queueItem.getBaseUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBaseUrl());
    }
}
