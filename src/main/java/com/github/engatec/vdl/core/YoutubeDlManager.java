package com.github.engatec.vdl.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.youtubedl.processbuilder.CacheRemoveProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.DownloadProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.DownloadWithConfigFileProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.DownloadableInfoFetchProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.VersionFetchProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.YoutubeDlProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.YoutubeDlUpdateProcessBuilder;
import com.github.engatec.vdl.exception.ProcessException;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.util.AppUtils;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YoutubeDlManager {

    private static final Logger LOGGER = LogManager.getLogger(YoutubeDlManager.class);

    private final ApplicationContext ctx = ApplicationContext.getInstance();

    public static final YoutubeDlManager INSTANCE = new YoutubeDlManager();

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public List<VideoInfo> fetchDownloadableInfo(List<String> urls) throws IOException {
        var pb = new DownloadableInfoFetchProcessBuilder(urls);
        List<String> command = pb.buildCommand();
        List<VideoInfo> videoInfoList;
        Process process = pb.buildProcess(command);
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            List<String> jsonList = reader.lines().toList();
            videoInfoList = new ArrayList<>(jsonList.size());
            for (String json : jsonList) {
                VideoInfo videoInfo = objectMapper.readValue(json, VideoInfo.class);
                if (StringUtils.isBlank(videoInfo.getBaseUrl())) {
                    videoInfo.setBaseUrl(videoInfo.getUrl());
                }
                videoInfoList.add(videoInfo);
            }
        } catch (Exception e) {
            LOGGER.error("Failed command: '{}'", String.join(StringUtils.SPACE, command));
            checkErrors(process);
            throw e;
        }

        checkErrors(process);

        return videoInfoList;
    }

    /**
     * @throws ProcessException if the process contained any errors
     */
    private void checkErrors(Process process) {
        String errorMsg;
        try (InputStream errorStream = process.getErrorStream()) {
            errorMsg = IOUtils.readLines(errorStream, ctx.getSystemCharset()).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            errorMsg = e.getMessage();
        }

        if (StringUtils.isNotBlank(errorMsg)) {
            LOGGER.warn(errorMsg);
            throw new ProcessException(errorMsg);
        }
    }

    public Process download(Downloadable downloadable) throws IOException {
        Boolean useConfigFile = ctx.getConfigRegistry().get(UseConfigFilePref.class).getValue();
        YoutubeDlProcessBuilder pb = useConfigFile ? new DownloadWithConfigFileProcessBuilder(downloadable) : new DownloadProcessBuilder(downloadable);
        List<String> command = pb.buildCommand();
        return pb.buildProcess(command);
    }

    public String getCurrentVersion(Engine engine) {
        String version = null;

        try {
            var pb = new VersionFetchProcessBuilder(engine);
            List<String> command = pb.buildCommand();
            Process process = pb.buildProcess(command);
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                version = reader.lines().findFirst().orElseThrow(ProcessException::new);
            } catch (Exception e) {
                try (InputStream errorStream = process.getErrorStream()) {
                    IOUtils.readLines(errorStream, ctx.getSystemCharset()).forEach(LOGGER::warn);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return version;
    }

    public void updateYoutubeDl() throws IOException, InterruptedException {
        doUpdate(Engine.YOUTUBE_DL);
    }

    public void updateYtdlp() throws IOException, InterruptedException {
        doUpdate(Engine.YT_DLP);
    }

    private void doUpdate(Engine engine) throws IOException, InterruptedException {
        // LastModifiedTime is a bit "hacky" solution, but I need to be sure that the file will have actually updated
        FileTime initialLastModifiedTime = Files.getLastModifiedTime(ctx.getDownloaderPath(engine));

        List<YoutubeDlProcessBuilder> processBuilders = List.of(new CacheRemoveProcessBuilder(), new YoutubeDlUpdateProcessBuilder(engine));
        String currentVersion = getCurrentVersion(engine);
        boolean versionIsUpToDate = false;
        for (YoutubeDlProcessBuilder pb : processBuilders) {
            List<String> command = pb.buildCommand();
            Process process = pb.buildProcess(command);
            try (InputStream is = process.getInputStream()) {
                versionIsUpToDate |= IOUtils.readLines(is, ctx.getSystemCharset())
                        .stream()
                        .filter(Objects::nonNull)
                        .peek(it -> {
                            LOGGER.info(it);
                            if (StringUtils.startsWith(it, "ERROR:")) {
                                throw new ProcessException(it);
                            }
                        })
                        .anyMatch(it -> it.contains(currentVersion));
            }
            checkErrors(process);
            process.waitFor();
        }

        if (versionIsUpToDate) {
            return;
        }

        FileTime currentLastModifiedTime = initialLastModifiedTime;
        while (currentLastModifiedTime.compareTo(initialLastModifiedTime) == 0) {
            try {
                currentLastModifiedTime = Files.getLastModifiedTime(ctx.getDownloaderPath(engine));
                TimeUnit.SECONDS.sleep(1);
            } catch (IOException ignored) { // For extremely rare cases when getLastModifiedTime() is called when the old file already removed, but the new one hasn't been renamed yet
                // ignore
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void checkLatestYoutubeDlVersion(Stage stage) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/ytdl-org/youtube-dl/releases/latest"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        String latestVersion = String.valueOf(objectMapper.readValue(json, HashMap.class).get("tag_name"));
                        String currentVersion = getCurrentVersion(Engine.YOUTUBE_DL);
                        latestVersion = RegExUtils.replaceAll(latestVersion, "\\.", "");
                        currentVersion = RegExUtils.replaceAll(currentVersion, "\\.", "");
                        if (Integer.parseInt(latestVersion) > Integer.parseInt(currentVersion)) {
                            Platform.runLater(() -> Dialogs.infoWithYesNoButtons(
                                    "youtubedl.update.available",
                                    () -> AppUtils.updateYoutubeDl(stage, null),
                                    null
                            ));
                        }
                    } catch (Exception e) { // No need to fail if version check went wrong
                        LOGGER.warn(e.getMessage());
                    }
                });
    }

    public void checkLatestYtdlpVersion(Stage stage) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/yt-dlp/yt-dlp/releases/latest"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> {
                    try {
                        String latestVersion = String.valueOf(objectMapper.readValue(json, HashMap.class).get("tag_name"));
                        String currentVersion = getCurrentVersion(Engine.YT_DLP);
                        latestVersion = RegExUtils.replaceAll(latestVersion, "\\.", "");
                        currentVersion = RegExUtils.replaceAll(currentVersion, "\\.", "");
                        if (Integer.parseInt(latestVersion) > Integer.parseInt(currentVersion)) {
                            Platform.runLater(() -> Dialogs.infoWithYesNoButtons(
                                    "ytdlp.update.available",
                                    () -> AppUtils.updateYtdlp(stage, null),
                                    null
                            ));
                        }
                    } catch (Exception e) { // No need to fail if version check went wrong
                        LOGGER.warn(e.getMessage());
                    }
                });
    }
}
