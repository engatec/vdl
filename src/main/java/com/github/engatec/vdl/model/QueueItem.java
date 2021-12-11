package com.github.engatec.vdl.model;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.engatec.vdl.model.downloadable.BaseDownloadable;
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

    private Long id;
    private final Downloadable downloadable;

    private final ObjectProperty<DownloadStatus> status = new SimpleObjectProperty<>(DownloadStatus.READY);
    private final DoubleProperty progress = new SimpleDoubleProperty();
    private final StringProperty size = new SimpleStringProperty();
    private final StringProperty throughput = new SimpleStringProperty();
    private final Set<String> destinations = Collections.synchronizedSet(new HashSet<>(2)); // Normally there's 1 or 2 items downloaded: video / video+audio

    @SuppressWarnings("unused")
    public QueueItem() {
        this(new BaseDownloadable());
    }

    public QueueItem(Downloadable downloadable) {
        this.downloadable = downloadable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return downloadable.getTitle();
    }

    @Override
    public void setTitle(String title) {
        downloadable.setTitle(title);
    }

    @Override
    public String getFormatId() {
        return downloadable.getFormatId();
    }

    @Override
    public void setFormatId(String formatId) {
        downloadable.setFormatId(formatId);
    }

    @Override
    public String getBaseUrl() {
        return downloadable.getBaseUrl();
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        downloadable.setBaseUrl(baseUrl);
    }

    @Override
    public Path getDownloadPath() {
        return downloadable.getDownloadPath();
    }

    @Override
    public void setDownloadPath(Path downloadPath) {
        downloadable.setDownloadPath(downloadPath);
    }

    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public double getProgress() {
        return progress.get();
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public DownloadStatus getStatus() {
        return status.get();
    }

    public void setStatus(DownloadStatus status) {
        this.status.set(status);
    }

    public ObjectProperty<DownloadStatus> statusProperty() {
        return status;
    }

    public String getThroughput() {
        return throughput.get();
    }

    public void setThroughput(String throughput) {
        this.throughput.set(throughput);
    }

    public StringProperty throughputProperty() {
        return throughput;
    }

    /**
     * Use this getter to add items to the collection only. Do not use it for iterations as it may lead to ConcurrentModificationException.
     * For iterations use {@link #getDestinationsForTraversal()}.
     */
    public Set<String> getDestinations() {
        return destinations;
    }

    public Set<String> getDestinationsForTraversal() {
        synchronized (destinations) {
            return new HashSet<>(destinations);
        }
    }

    public void setDestinations(Collection<String> destinations) {
        this.destinations.clear();
        this.destinations.addAll(destinations);
    }

    @Override
    public List<Postprocessing> getPostprocessingSteps() {
        return downloadable.getPostprocessingSteps();
    }

    @Override
    public void setPostprocessingSteps(List<Postprocessing> items) {
        downloadable.setPostprocessingSteps(List.copyOf(ListUtils.emptyIfNull(items)));
    }
}
