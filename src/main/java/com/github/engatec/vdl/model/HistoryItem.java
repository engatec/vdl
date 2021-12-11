package com.github.engatec.vdl.model;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HistoryItem {

    private String title;
    private String url;
    // FIXME: JsonProperty annotations to support transition from JSON to sqlite. For removal in 1.9
    @JsonProperty("path") private Path downloadPath;
    @JsonProperty("dtm") private String createdAt;

    public HistoryItem() {
    }

    public HistoryItem(String title, String url, Path downloadPath) {
        this(title, url, downloadPath, null);
    }

    public HistoryItem(String title, String url, Path downloadPath, String createdAt) {
        this.title = title;
        this.url = url;
        this.downloadPath = downloadPath;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Path getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(Path downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
