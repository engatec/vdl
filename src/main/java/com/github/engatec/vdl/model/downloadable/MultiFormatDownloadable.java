package com.github.engatec.vdl.model.downloadable;

import java.util.List;

import org.apache.commons.collections4.ListUtils;

public class MultiFormatDownloadable extends BaseDownloadable {

    private final List<Video> videos;
    private final List<Audio> audios;

    public MultiFormatDownloadable(String title, Integer duration, String baseUrl, List<Video> videos, List<Audio> audios) {
        super(title, duration, baseUrl);
        this.videos = videos;
        this.audios = audios;
    }

    public List<Video> getVideos() {
        return ListUtils.emptyIfNull(videos);
    }

    public List<Audio> getAudios() {
        return ListUtils.emptyIfNull(audios);
    }
}
