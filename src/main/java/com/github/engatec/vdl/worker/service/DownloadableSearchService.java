package com.github.engatec.vdl.worker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlAttr;
import com.github.engatec.vdl.exception.NoDownloadableFoundException;
import com.github.engatec.vdl.model.Format;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.Video;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.collections4.CollectionUtils;

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
                List<VideoInfo> videoInfoList = YoutubeDlManager.INSTANCE.fetchVideoInfo(getUrl());
                if (CollectionUtils.isEmpty(videoInfoList)) {
                    throw new NoDownloadableFoundException();
                }

                return videoInfoList.stream()
                        .map(DownloadableSearchService.this::prepareDownloadable)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
        };
    }

    private MultiFormatDownloadable prepareDownloadable(VideoInfo videoInfo) {
        List<Format> formats = videoInfo.getFormats();
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
        setTrackNo(audioList);

        return new MultiFormatDownloadable(videoInfo.getTitle(), videoInfo.getBaseUrl(), videoList, audioList);
    }

    private void setTrackNo(List<Audio> audioList) {
        for (int i = 0; i < audioList.size(); i++) {
            Audio audio = audioList.get(i);
            audio.setTrackNo(i + 1);
        }
    }
}
