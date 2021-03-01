package com.github.engatec.vdl.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

public class QueueItem implements Downloadable {

    private final ObjectProperty<DownloadStatus> status = new SimpleObjectProperty<>(DownloadStatus.READY);
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final StringProperty size = new SimpleStringProperty();
    private final StringProperty throughput = new SimpleStringProperty();

    private String baseUrl;
    private Path downloadPath;
    private String formatId;

    private List<Postprocessing> postprocessingSteps;

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

    @JsonIgnore
    @Override
    public String getTitle() {
        return StringUtils.EMPTY;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public List<Postprocessing> getPostprocessingSteps() {
        return ListUtils.emptyIfNull(postprocessingSteps);
    }

    @Override
    public void setPostprocessingSteps(List<Postprocessing> items) {
        postprocessingSteps = List.copyOf(ListUtils.emptyIfNull(items));
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
        return baseUrl.equals(queueItem.baseUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl);
    }
}
