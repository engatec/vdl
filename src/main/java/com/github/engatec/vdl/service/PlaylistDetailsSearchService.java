package com.github.engatec.vdl.service;

import java.util.List;

import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.service.task.BasicVideoInfoSearchTask;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class PlaylistDetailsSearchService extends Service<List<VideoInfo>> {

    private final StringProperty url = new SimpleStringProperty();

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
