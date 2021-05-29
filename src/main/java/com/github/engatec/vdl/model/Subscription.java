package com.github.engatec.vdl.model;

import java.util.HashSet;
import java.util.Set;

public class Subscription {

    private String name;
    private String url;
    private Set<String> processedItems;
    private String path;
    private String createdAt;

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

    public Set<String> getProcessedItems() {
        if (processedItems == null) {
            processedItems = new HashSet<>();
        }
        return processedItems;
    }

    public void setProcessedItems(Set<String> processedItems) {
        this.processedItems = processedItems;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
