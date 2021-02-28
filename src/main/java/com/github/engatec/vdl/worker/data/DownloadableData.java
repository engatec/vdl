package com.github.engatec.vdl.worker.data;

import java.util.ArrayList;
import java.util.List;

import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.downloadable.Video;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;

public class DownloadableData {

    private final String title;
    private final String baseUrl;
    private final List<Video> videoList;
    private final List<Audio> audioList;
    private final List<Postprocessing> postprocessingList = new ArrayList<>();

    public DownloadableData(String title, String baseUrl, List<Video> videoList, List<Audio> audioList) {
        this.title = title;
        this.baseUrl = baseUrl;
        this.videoList = videoList;
        this.audioList = audioList;
    }

    public String getTitle() {
        return title;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public List<Audio> getAudioList() {
        return audioList;
    }

    public List<Postprocessing> getPostprocessingList() {
        return postprocessingList;
    }
}
