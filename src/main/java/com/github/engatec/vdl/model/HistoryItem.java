package com.github.engatec.vdl.model;

import java.nio.file.Path;

public class HistoryItem {

    private String title;
    private String url;
    private Path path;
    private String dtm;

    public HistoryItem() {
    }

    public HistoryItem(String title, String url, Path path, String dtm) {
        this.title = title;
        this.url = url;
        this.path = path;
        this.dtm = dtm;
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

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getDtm() {
        return dtm;
    }

    public void setDtm(String dtm) {
        this.dtm = dtm;
    }
}
