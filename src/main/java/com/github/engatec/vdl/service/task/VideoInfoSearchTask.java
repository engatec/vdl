package com.github.engatec.vdl.service.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.exception.ProcessException;
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
        return extractVideoInfo(urls, new HashSet<>());
    }

    /**
     * @param processedUrls - this Set protects from a youtube bug when a playlist tab url is incorrect and recursively appears again and again turning this search into an infinite loop
     *                      which eventually will crash with either StackOverflowError or OutOfMemoryError
     */
    private List<VideoInfo> extractVideoInfo(List<String> urls, Set<String> processedUrls) throws IOException {
        if (Thread.interrupted()) {
            cancel();
        }

        if (isCancelled()) {
            return List.of();
        }

        List<VideoInfo> items = List.of();
        try {
            items = YoutubeDlManager.INSTANCE.fetchDownloadableInfo(urls);
        } catch (ProcessException e) {
            updateMessage(e.getMessage());
        }

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
                    .filter(Predicate.not(processedUrls::contains))
                    .toList();

            processedUrls.addAll(playlistUrls);

            if (CollectionUtils.isNotEmpty(playlistUrls)) {
                videos.addAll(extractVideoInfo(playlistUrls, processedUrls));
            }
        }

        return videos;
    }
}
