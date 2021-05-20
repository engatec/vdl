package com.github.engatec.vdl.worker.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.exception.NoDownloadableFoundException;
import com.github.engatec.vdl.model.VideoInfo;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

public class DownloadableSearchService extends Service<List<VideoInfo>> {

    private static final int CONCURRENT_PLAYLIST_ITEMS_FETCH_COUNT = 5;

    private final StringProperty url = new SimpleStringProperty();
    private final ObjectProperty<BiConsumer<List<VideoInfo>, Integer>> onInfoFetchedCallback = new SimpleObjectProperty<>();

    public DownloadableSearchService() {
        super();
        setExecutor(AppExecutors.COMMON_EXECUTOR);
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    public BiConsumer<List<VideoInfo>, Integer> getOnInfoFetchedCallback() {
        return onInfoFetchedCallback.get();
    }

    public void setOnInfoFetchedCallback(BiConsumer<List<VideoInfo>, Integer> onInfoFetchedCallback) {
        this.onInfoFetchedCallback.set(onInfoFetchedCallback);
    }

    @Override
    protected Task<List<VideoInfo>> createTask() {
        return new Task<>() {
            @Override
            protected List<VideoInfo> call() throws Exception {
                List<VideoInfo> allFoundItems = extractVideoInfo(List.of(getUrl()));
                if (CollectionUtils.isEmpty(allFoundItems)) {
                    throw new NoDownloadableFoundException();
                }

                int totalCount = allFoundItems.size();
                List<VideoInfo> basicItemsInfoList = new ArrayList<>();
                List<VideoInfo> completeItemsInfoList = new ArrayList<>();
                for (VideoInfo item : allFoundItems) {
                    if (isCompleteVideoInfo(item)) {
                        completeItemsInfoList.add(item);
                    } else {
                        basicItemsInfoList.add(item);
                    }
                }

                perfomProgressUpdate(new ArrayList<>(completeItemsInfoList), completeItemsInfoList.size(), totalCount);

                List<String> playlistVideoUrls = basicItemsInfoList.stream().map(VideoInfo::getBaseUrl).collect(Collectors.toList());
                for (List<String> urls : ListUtils.partition(playlistVideoUrls, CONCURRENT_PLAYLIST_ITEMS_FETCH_COUNT)) {
                    if (Thread.interrupted()) {
                        cancel();
                    }

                    if (isCancelled()) {
                        break;
                    }

                    List<VideoInfo> chunk = YoutubeDlManager.INSTANCE.fetchDownloadableInfo(urls);
                    completeItemsInfoList.addAll(chunk);
                    perfomProgressUpdate(chunk, completeItemsInfoList.size(), totalCount);
                }

                return completeItemsInfoList;
            }

            /**
             * Method to handle "inner" playlists. For example a link to a channel has been provided an the channel contains multiple playlists. Info from them must be properly extracted.
             */
            private List<VideoInfo> extractVideoInfo(List<String> urls) throws IOException {
                List<VideoInfo> items = YoutubeDlManager.INSTANCE.fetchDownloadableInfo(urls);

                List<VideoInfo> playlists = new ArrayList<>();
                List<VideoInfo> videos = new ArrayList<>();

                for (VideoInfo item : items) {
                    if (isCompleteVideoInfo(item)) {
                        videos.add(item);
                    } else if (StringUtils.isNotBlank(item.getId()) || StringUtils.isNotBlank(item.getExtractor())) { // Highly likely a link to a complete video info
                        videos.add(item);
                    } else {
                        playlists.add(item);
                    }
                }

                if (CollectionUtils.isNotEmpty(playlists)) {
                    List<String> newUrls = playlists.stream()
                            .map(VideoInfo::getBaseUrl)
                            .collect(Collectors.toList());

                    videos.addAll(extractVideoInfo(newUrls));
                }

                return videos;
            }

            private boolean isCompleteVideoInfo(VideoInfo item) {
                String type = item.getType();
                return StringUtils.isBlank(type) || type.equals("video");
            }

            private void perfomProgressUpdate(List<VideoInfo> chunk, int readyCount, int totalCount) {
                updateMessage(String.format(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.main.search.playlist.progress"), readyCount, totalCount));
                updateProgress(readyCount, totalCount);
                Platform.runLater(() -> getOnInfoFetchedCallback().accept(chunk, totalCount));
            }
        };
    }
}
