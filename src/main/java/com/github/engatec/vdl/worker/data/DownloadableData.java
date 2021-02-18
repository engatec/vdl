package com.github.engatec.vdl.worker.data;

import java.util.List;

import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.downloadable.Video;

public class DownloadableData {

    private final List<Video> videoList;
    private final List<Audio> audioList;

    public DownloadableData(List<Video> videoList, List<Audio> audioList) {
        this.videoList = videoList;
        this.audioList = audioList;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public List<Audio> getAudioList() {
        return audioList;
    }
}
