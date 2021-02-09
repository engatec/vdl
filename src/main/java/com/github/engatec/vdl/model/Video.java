package com.github.engatec.vdl.model;

import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;

public class Video implements Downloadable {

    private final String baseUrl;
    private final Format format;
    private Audio audio;

    public Video(String baseUrl, Format format) {
        this(baseUrl, format, null);
    }

    public Video(String baseUrl, Format format, Audio audio) {
        this.baseUrl = baseUrl;
        this.format = format;
        this.audio = audio;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getFormatId() {
        String formatId = getId();
        if (audio != null && !Objects.equals(formatId, audio.getFormatId())) {
            formatId += "+" + audio.getFormatId();
        }
        return formatId;
    }

    public String getId() {
        return format.getId();
    }

    public String getExtension() {
        return format.getExtension();
    }

    public Double getBitrate() {
        return ObjectUtils.defaultIfNull(format.getVideoBitrate(), format.getTotalBitrate());
    }

    public Integer getWidth() {
        return format.getWidth();
    }

    public Integer getHeight() {
        return format.getHeight();
    }

    public Long getFilesize() {
        return format.getFilesize();
    }

    public String getCodec() {
        return format.getVcodec();
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }
}
