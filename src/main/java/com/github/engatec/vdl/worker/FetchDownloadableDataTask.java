package com.github.engatec.vdl.worker;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.github.engatec.vdl.core.DownloadManager;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlAttr;
import com.github.engatec.vdl.exception.NoDownloadableFoundException;
import com.github.engatec.vdl.model.Audio;
import com.github.engatec.vdl.model.Format;
import com.github.engatec.vdl.model.Video;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.worker.data.DownloadableData;
import javafx.concurrent.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

public class FetchDownloadableDataTask extends Task<DownloadableData> {

    private static final Logger LOGGER = LogManager.getLogger(FetchDownloadableDataTask.class);

    private static final int MIN_CONTENT_LENGTH = 307200; // 300kb
    private static final int MAX_TIMEOUT_SECONDS = 30;

    private final String url;
    private final HttpClient client;

    public FetchDownloadableDataTask(String url) {
        super();
        this.url = url;
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(MAX_TIMEOUT_SECONDS))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    @Override
    protected DownloadableData call() throws Exception {
        VideoInfo videoInfo = DownloadManager.INSTANCE.fetchVideoInfo(url);
        List<Format> formats = ListUtils.emptyIfNull(videoInfo.getFormats());
        if (CollectionUtils.isEmpty(formats)) {
            throw new NoDownloadableFoundException();
        }

        List<Video> videoList = new ArrayList<>();
        List<Audio> audioList = new ArrayList<>();
        final String codecAbsenseAttr = YoutubeDlAttr.NO_CODEC.getValue();

        for (Format format : formats) {
            String acodec = format.getAcodec();
            String vcodec = format.getVcodec();
            if (codecAbsenseAttr.equals(vcodec)) {
                audioList.add(new Audio(url, format));
            } else if (codecAbsenseAttr.equals(acodec)) {
                videoList.add(new Video(url, format));
            } else {
                videoList.add(new Video(url, format, new Audio(url, format)));
            }
        }

        ensureFileSize(formats);

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

        return new DownloadableData(videoList, audioList);
    }

    private void setTrackNo(List<Audio> audioList) {
        for (int i = 0; i < audioList.size(); i++) {
            Audio audio = audioList.get(i);
            audio.setTrackNo(i + 1);
        }
    }

    private void ensureFileSize(List<Format> formats) {
        List<CompletableFuture<Void>> cf = new ArrayList<>();
        for (Format format : formats) {
            if (format.getFilesize() == null) {
                cf.add(tryResolveFileSizeAsync(format).exceptionally(t -> {
                    LOGGER.warn(t.getMessage(), t);
                    return null;
                }));
            }
        }

        CompletableFuture.allOf(cf.toArray(new CompletableFuture[] {})).join();
    }

    private CompletableFuture<Void> tryResolveFileSizeAsync(final Format format) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(format.getUrl()))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(MAX_TIMEOUT_SECONDS));

        for (var header : MapUtils.emptyIfNull(format.getHttpHeaders()).entrySet()) {
            requestBuilder.setHeader(header.getKey(), header.getValue());
        }

        return client.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        response.headers().firstValue("content-length").ifPresent(contentLength -> {
                            try {
                                long filesize = Long.parseLong(contentLength);
                                /* Перестрахуемся. Если дошли до сюда, значит youtube-dl не смог определить размер видео.
                                   По факту этот метод - костыль, попытка напрямую спросить ресурс о его размере.
                                   Но если ответ слишком маленький, есть основания полагать, что это не видео/аудио мелкого размера,
                                   а вернулась какая-то дичь, поэтому скипнем такой размер на всякий случай. */
                                if (filesize < MIN_CONTENT_LENGTH) {
                                    return;
                                }
                                format.setFilesize(filesize);
                            } catch (Exception ignored) {
                            }
                        });
                    }
                });
    }
}
