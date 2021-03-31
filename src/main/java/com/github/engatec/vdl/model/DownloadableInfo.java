package com.github.engatec.vdl.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DownloadableInfo {

    @JsonProperty("webpage_url")
    private String baseUrl;
    // url property used by playlists as they don't have 'webpage_url'. One might think "Let's try @JsonAlias on baseUrl instead of separate url parameter"... don't!
    // Jackson doesn't prioritize @JsonProperty over @JsonAlias (at least current version) so you never know which of two will be chosen and it leads to breaking standalone
    // videos downloading on some resources. A better option is to check whether baseUrl property is blank after deserialization and if yes set it from url property.
    private String url;
    private String title;
    private List<Format> formats;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }
}
