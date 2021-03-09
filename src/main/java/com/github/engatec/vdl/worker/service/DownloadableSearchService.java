package com.github.engatec.vdl.worker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlAttr;
import com.github.engatec.vdl.exception.NoDownloadableFoundException;
import com.github.engatec.vdl.model.DownloadableInfo;
import com.github.engatec.vdl.model.Format;
import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.Video;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

public class DownloadableSearchService extends Service<List<MultiFormatDownloadable>> {

    private final StringProperty url = new SimpleStringProperty();

    public DownloadableSearchService() {
        super();
        setExecutor(ApplicationContext.INSTANCE.getSharedExecutor());
    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }

    @Override
    protected Task<List<MultiFormatDownloadable>> createTask() {
        return new Task<>() {
            @Override
            protected List<MultiFormatDownloadable> call() throws Exception {
                List<DownloadableInfo> foundItems = YoutubeDlManager.INSTANCE.fetchDownloadableInfo(getUrl());
                if (CollectionUtils.isEmpty(foundItems)) {
                    throw new NoDownloadableFoundException();
                }

                int totalItemsCount = foundItems.size();
                List<MultiFormatDownloadable> downloadables = new ArrayList<>(totalItemsCount);
                for (DownloadableInfo item : foundItems) {
                    if (Thread.interrupted()) {
                        cancel();
                    }

                    if (isCancelled()) {
                        break;
                    }

                    if (CollectionUtils.isEmpty(item.getFormats())) { // Empty formats mean this is a playlist, so search again for the actual data
                        List<DownloadableInfo> infos = YoutubeDlManager.INSTANCE.fetchDownloadableInfo(item.getBaseUrl());
                        downloadables.addAll(
                                ListUtils.emptyIfNull(infos).stream()
                                        .map(DownloadableSearchService.this::prepareDownloadable)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList())
                        );
                    } else {
                        MultiFormatDownloadable downloadable = prepareDownloadable(item);
                        if (downloadable != null) {
                            downloadables.add(downloadable);
                        }
                    }

                    int currentItemsCount = downloadables.size();
                    updateMessage(String.format(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.main.search.playlist.progress"), currentItemsCount, totalItemsCount));
                    updateProgress(currentItemsCount, totalItemsCount);
                }

                return downloadables;
            }
        };
    }

    private MultiFormatDownloadable prepareDownloadable(DownloadableInfo downloadableInfo) {
        List<Format> formats = downloadableInfo.getFormats();
        if (CollectionUtils.isEmpty(formats)) {
            return null;
        }

        List<Video> videoList = new ArrayList<>();
        List<Audio> audioList = new ArrayList<>();
        final String codecAbsenseAttr = YoutubeDlAttr.NO_CODEC.getValue();

        for (Format format : formats) {
            String acodec = format.getAcodec();
            String vcodec = format.getVcodec();
            if (codecAbsenseAttr.equals(vcodec)) {
                audioList.add(new Audio(format));
            } else if (codecAbsenseAttr.equals(acodec)) {
                videoList.add(new Video(format));
            } else {
                videoList.add(new Video(format, new Audio(format)));
            }
        }

        videoList.sort(
                comparing(Video::getWidth, nullsFirst(naturalOrder()))
                        .thenComparing(Video::getHeight, nullsFirst(naturalOrder()))
                        .thenComparing(Video::getFilesize, nullsFirst(naturalOrder()))
                        .reversed()
        );

        audioList.sort(
                comparing(Audio::getBitrate, nullsFirst(naturalOrder()))
                        .thenComparing(Audio::getFilesize, nullsFirst(naturalOrder()))
                        .reversed()
        );

        return new MultiFormatDownloadable(downloadableInfo.getTitle(), downloadableInfo.getBaseUrl(), videoList, audioList);
    }
}
