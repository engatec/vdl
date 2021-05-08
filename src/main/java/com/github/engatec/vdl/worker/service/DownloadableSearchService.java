package com.github.engatec.vdl.worker.service;

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
                List<VideoInfo> allItems = YoutubeDlManager.INSTANCE.fetchDownloadableInfo(List.of(getUrl()));
                if (CollectionUtils.isEmpty(allItems)) {
                    throw new NoDownloadableFoundException();
                }

                int totalCount = allItems.size();
                List<VideoInfo> readyItems = new ArrayList<>();
                List<VideoInfo> playlistItems = new ArrayList<>();
                for (VideoInfo item : allItems) {
                    if (CollectionUtils.isNotEmpty(item.getFormats())) {
                        readyItems.add(item);
                    } else {
                        playlistItems.add(item);
                    }
                }

                perfomProgressUpdate(new ArrayList<>(readyItems), readyItems.size(), totalCount);

                List<String> playlistVideoUrls = playlistItems.stream().map(VideoInfo::getBaseUrl).collect(Collectors.toList());
                for (List<String> urls : ListUtils.partition(playlistVideoUrls, CONCURRENT_PLAYLIST_ITEMS_FETCH_COUNT)) {
                    if (Thread.interrupted()) {
                        cancel();
                    }

                    if (isCancelled()) {
                        break;
                    }

                    List<VideoInfo> chunk = YoutubeDlManager.INSTANCE.fetchDownloadableInfo(urls);
                    readyItems.addAll(chunk);
                    perfomProgressUpdate(chunk, readyItems.size(), totalCount);
                }

                return readyItems;
            }

            private void perfomProgressUpdate(List<VideoInfo> chunk, int readyCount, int totalCount) {
                updateMessage(String.format(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.main.search.playlist.progress"), readyCount, totalCount));
                updateProgress(readyCount, totalCount);
                Platform.runLater(() -> getOnInfoFetchedCallback().accept(chunk, totalCount));
            }
        };
    }
}
