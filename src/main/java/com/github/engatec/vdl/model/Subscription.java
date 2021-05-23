package com.github.engatec.vdl.model;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

public class Subscription {

    private String title;
    private String playlistUrl;
    private List<String> processedItems;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    public List<String> getProcessedItems() {
        return Collections.unmodifiableList(ListUtils.emptyIfNull(processedItems));
    }

    public void setProcessedItems(List<String> processedItems) {
        this.processedItems = processedItems;
    }
}
