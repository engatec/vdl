package com.github.engatec.vdl.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Format {

    @JsonProperty("format_id")
    private String id;

    @JsonProperty("ext")
    private String extension;

    @JsonProperty("tbr")
    private Double totalBitrate;

    @JsonProperty("vbr")
    private Double videoBitrate;

    @JsonProperty("abr")
    private Double audioBitrate;

    private Integer width;
    private Integer height;
    private Long filesize;
    private String vcodec;
    private String acodec;
    private Double fps;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Double getTotalBitrate() {
        return totalBitrate;
    }

    public void setTotalBitrate(Double totalBitrate) {
        this.totalBitrate = totalBitrate;
    }

    public Double getVideoBitrate() {
        return videoBitrate;
    }

    public void setVideoBitrate(Double videoBitrate) {
        this.videoBitrate = videoBitrate;
    }

    public Double getAudioBitrate() {
        return audioBitrate;
    }

    public void setAudioBitrate(Double audioBitrate) {
        this.audioBitrate = audioBitrate;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public String getVcodec() {
        return vcodec;
    }

    public void setVcodec(String vcodec) {
        this.vcodec = vcodec;
    }

    public String getAcodec() {
        return acodec;
    }

    public void setAcodec(String acodec) {
        this.acodec = acodec;
    }

    public Double getFps() {
        return fps;
    }

    public void setFps(Double fps) {
        this.fps = fps;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
