package com.github.engatec.vdl.service.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.util.YouDlUtils;
import javafx.concurrent.Task;
import org.apache.commons.collections4.CollectionUtils;

public class VideoInfoSearchTask extends Task<List<VideoInfo>> {

    private final List<String> urls;

    public VideoInfoSearchTask(List<String> urls) {
        this.urls = urls;
    }

    @Override
    protected List<VideoInfo> call() throws Exception {
        return extractVideoInfo(urls);
    }

    /**
     * Method to handle "inner" playlists. For example a link to a channel has been provided an the channel contains multiple playlists. Info from them must be properly extracted.
     */
    private List<VideoInfo> extractVideoInfo(List<String> urls) throws IOException {
        if (Thread.interrupted()) {
            cancel();
        }

        if (isCancelled()) {
            return List.of();
        }

        List<VideoInfo> items = YoutubeDlManager.INSTANCE.fetchDownloadableInfo(urls);

        List<VideoInfo> playlists = new ArrayList<>();
        List<VideoInfo> videos = new ArrayList<>();

        for (VideoInfo item : items) {
            if (YouDlUtils.isPlaylist(item)) {
                playlists.add(item);
            } else {
                videos.add(item);
            }
        }

        if (CollectionUtils.isNotEmpty(playlists)) {
            List<String> playlistUrls = playlists.stream()
                    .map(VideoInfo::baseUrl)
                    .toList();

            videos.addAll(extractVideoInfo(playlistUrls));
        }

        return videos;
    }
}
