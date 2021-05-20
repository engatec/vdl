package com.github.engatec.vdl.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Parameters explanation can be found here: https://github.com/ytdl-org/youtube-dl/blob/master/youtube_dl/extractor/common.py
 */
public class VideoInfo {

    private String id;

    @JsonProperty("ie_key")
    private String extractor;

    @JsonProperty("_type")
    private String type;

    @JsonProperty("webpage_url")
    private String baseUrl;
    // url property used by playlists as they don't have 'webpage_url'. One might think "Let's try @JsonAlias on baseUrl instead of separate url parameter"... don't!
    // Jackson doesn't prioritize @JsonProperty over @JsonAlias (at least current version) so you never know which of two will be chosen and it leads to breaking standalone
    // videos downloading on some resources. A better option is to check whether baseUrl property is blank after deserialization and if yes set it from url property.
    private String url;
    private String title;
    private Integer duration;
    private List<Format> formats;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtractor() {
        return extractor;
    }

    public void setExtractor(String extractor) {
        this.extractor = extractor;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }
}
