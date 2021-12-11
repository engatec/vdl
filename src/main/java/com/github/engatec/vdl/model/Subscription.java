package com.github.engatec.vdl.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Subscription {

    private Long id;
    private String name;
    private String url;
    // FIXME: JsonProperty annotations to support transition from JSON to sqlite. For removal in 1.9
    @JsonProperty("path") private String downloadPath;
    private String createdAt;
    private final Set<String> processedItems = Collections.synchronizedSet(new HashSet<>());

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Use this getter to add items to the collection only. Do not use it for iterations as it may lead to ConcurrentModificationException.
     * For iterations use {@link #getProcessedItemsForTraversal()}.
     */
    public Set<String> getProcessedItems() {
        return processedItems;
    }

    public Set<String> getProcessedItemsForTraversal() {
        synchronized (processedItems) {
            return new HashSet<>(processedItems);
        }
    }

    public void setProcessedItems(Set<String> processedItems) {
        this.processedItems.clear();
        this.processedItems.addAll(processedItems);
    }
}
