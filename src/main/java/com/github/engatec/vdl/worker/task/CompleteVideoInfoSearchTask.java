package com.github.engatec.vdl.worker.task;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.model.VideoInfo;
import javafx.application.Platform;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

public class CompleteVideoInfoSearchTask extends VideoInfoSearchTask {

    private static final int CONCURRENT_PLAYLIST_ITEMS_FETCH_COUNT = 5;

    private final BiConsumer<List<VideoInfo>, Integer> onInfoFetchedCallback;

    public CompleteVideoInfoSearchTask(List<String> urls, BiConsumer<List<VideoInfo>, Integer> onInfoFetchedCallback) {
        super(urls);
        this.onInfoFetchedCallback = onInfoFetchedCallback;
    }

    @Override
    protected List<VideoInfo> call() throws Exception {
        List<VideoInfo> allFoundItems = super.call();
        if (CollectionUtils.isEmpty(allFoundItems)) {
            return List.of();
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

    private void perfomProgressUpdate(List<VideoInfo> chunk, int readyCount, int totalCount) {
        updateMessage(String.format(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.main.search.playlist.progress"), readyCount, totalCount));
        updateProgress(readyCount, totalCount);
        Platform.runLater(() -> onInfoFetchedCallback.accept(chunk, totalCount));
    }
}
