package com.github.engatec.vdl.service;

import java.util.List;
import java.util.function.BiConsumer;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.service.task.CompleteVideoInfoSearchTask;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DownloadableSearchService extends Service<List<VideoInfo>> {

    private final ListProperty<String> urls = new SimpleListProperty<>();
    private final ObjectProperty<BiConsumer<List<VideoInfo>, Integer>> onInfoFetchedCallback = new SimpleObjectProperty<>();

    public DownloadableSearchService() {
        super();
        setExecutor(ApplicationContext.getInstance().appExecutors().get(AppExecutors.Type.COMMON_EXECUTOR));
    }

    public List<String> getUrls() {
        return urls.get();
    }

    public void setUrls(List<String> urls) {
        this.urls.set(FXCollections.observableList(urls));
    }

    public BiConsumer<List<VideoInfo>, Integer> getOnInfoFetchedCallback() {
        return onInfoFetchedCallback.get();
    }

    public void setOnInfoFetchedCallback(BiConsumer<List<VideoInfo>, Integer> onInfoFetchedCallback) {
        this.onInfoFetchedCallback.set(onInfoFetchedCallback);
    }

    @Override
    protected Task<List<VideoInfo>> createTask() {
        return new CompleteVideoInfoSearchTask(getUrls(), getOnInfoFetchedCallback());
    }
}
