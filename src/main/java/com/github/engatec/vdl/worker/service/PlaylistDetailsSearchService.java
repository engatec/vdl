package com.github.engatec.vdl.worker.service;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.worker.task.BasicVideoInfoSearchTask;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class PlaylistDetailsSearchService extends Service<List<VideoInfo>> {

    private final StringProperty url = new SimpleStringProperty();

    public PlaylistDetailsSearchService() {
        this(AppExecutors.COMMON_EXECUTOR);
    }

    public PlaylistDetailsSearchService(ExecutorService executor) {
        super();
        setExecutor(executor);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    @Override
    protected Task<List<VideoInfo>> createTask() {
        return new BasicVideoInfoSearchTask(List.of(getUrl()));
    }
}
