package com.github.engatec.vdl.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.collections4.SetUtils;

public class Subscription {

    private String name;
    private String playlistUrl;
    private Set<String> processedItems;
    private LocalDateTime createdAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaylistUrl() {
        return playlistUrl;
    }

    public void setPlaylistUrl(String playlistUrl) {
        this.playlistUrl = playlistUrl;
    }

    public Set<String> getProcessedItems() {
        return Collections.unmodifiableSet(SetUtils.emptyIfNull(processedItems));
    }

    public void setProcessedItems(Set<String> processedItems) {
        this.processedItems = processedItems;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
