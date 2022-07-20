package com.github.engatec.vdl.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.youtubedl.processbuilder.CacheRemoveProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.DownloadableInfoFetchProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.VersionFetchProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.YoutubeDlProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.YoutubeDlUpdateProcessBuilder;
import com.github.engatec.vdl.exception.ProcessException;
import com.github.engatec.vdl.model.VideoInfo;
import org.apache.commons.io.IOUtils;
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
        List<VideoInfo> videoInfoList = new ArrayList<>();
        Process process = pb.buildProcess(command);
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            List<String> jsonList = reader.lines().toList();
            for (String json : jsonList) {
                VideoInfo videoInfo = objectMapper.readValue(json, VideoInfo.class);
                videoInfoList.add(videoInfo);
            }
        } catch (Exception e) {
            LOGGER.error("Failed command: '{}'", String.join(StringUtils.SPACE, command));
            LOGGER.error(e.getMessage(), e);
        }

        fetchProcessError(process).ifPresent(it -> {
            LOGGER.warn(it);
            process.destroy();
        });

        return videoInfoList;
    }

    private Optional<String> fetchProcessError(Process process) {
        String errorMsg;
        try (InputStream errorStream = process.getErrorStream()) {
            errorMsg = IOUtils.readLines(errorStream, ctx.getSystemCharset()).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            errorMsg = e.getMessage();
        }

        return StringUtils.isNotBlank(errorMsg) ? Optional.of(errorMsg) : Optional.empty();
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

    public void updateVersion(Engine engine) throws IOException, InterruptedException {
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
                                process.destroy();
                                throw new ProcessException(it);
                            }
                        })
                        .anyMatch(it -> it.contains(currentVersion));
            }
            fetchProcessError(process).ifPresent(it -> {
                LOGGER.warn(it);
                process.destroy();
                throw new ProcessException(it);
            });
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
}
