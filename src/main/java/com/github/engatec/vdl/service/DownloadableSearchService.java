package com.github.engatec.vdl.service;

import java.util.List;
import java.util.function.BiConsumer;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.service.task.CompleteVideoInfoSearchTask;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DownloadableSearchService extends Service<List<VideoInfo>> {

    private final StringProperty url = new SimpleStringProperty();
    private final ObjectProperty<BiConsumer<List<VideoInfo>, Integer>> onInfoFetchedCallback = new SimpleObjectProperty<>();

    public DownloadableSearchService() {
        super();
        setExecutor(AppExecutors.COMMON_EXECUTOR);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public BiConsumer<List<VideoInfo>, Integer> getOnInfoFetchedCallback() {
        return onInfoFetchedCallback.get();
    }

    public void setOnInfoFetchedCallback(BiConsumer<List<VideoInfo>, Integer> onInfoFetchedCallback) {
        this.onInfoFetchedCallback.set(onInfoFetchedCallback);
    }

    @Override
    protected Task<List<VideoInfo>> createTask() {
        return new CompleteVideoInfoSearchTask(List.of(getUrl()), getOnInfoFetchedCallback());
    }
}
