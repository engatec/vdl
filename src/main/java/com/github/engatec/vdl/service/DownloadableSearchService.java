package com.github.engatec.vdl.service;

import java.util.List;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.service.task.VideoInfoSearchTask;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class DownloadableSearchService extends Service<List<VideoInfo>> {

    private final ListProperty<String> urls = new SimpleListProperty<>();

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

    @Override
    protected Task<List<VideoInfo>> createTask() {
        return new VideoInfoSearchTask(getUrls());
    }
}
