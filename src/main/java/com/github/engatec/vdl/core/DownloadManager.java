package com.github.engatec.vdl.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.model.Downloadable;
import com.github.engatec.vdl.model.VideoInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadManager {

    private static final Logger LOGGER = LogManager.getLogger(DownloadManager.class);

    public static final DownloadManager INSTANCE = new DownloadManager();

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private Process runCommand(List<String> command) throws IOException {
        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
    }

    public VideoInfo fetchVideoInfo(String url) throws IOException {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url must not be blank");
        }

        List<String> command = YoutubeDlCommandBuilder.newInstance()
                .noDebug()
                .dumpJson()
                .url(url)
                .buildAsList();

        Process process = new ProcessBuilder(command).start();
        try (InputStream is = process.getInputStream()) {
            return objectMapper.readValue(is, VideoInfo.class);
        } catch (Exception e) {
            try (InputStream errorStream = process.getErrorStream()) {
                logError(errorStream);
            }
            LOGGER.error("Failed command: '{}'", String.join(StringUtils.SPACE, command));
            throw e;
        }
    }

    private void logError(InputStream errorStream) {
        try {
            if (errorStream.available() <= 0) {
                return;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        new BufferedReader(new InputStreamReader(errorStream)).lines().forEach(LOGGER::error);
    }

    public Process download(Downloadable downloadable, Path outputPath) throws IOException {
        List<String> command = YoutubeDlCommandBuilder.newInstance()
                .noDebug()
                .formatId(downloadable.getFormatId())
                .outputPath(outputPath)
                .ffmpegLocation(ApplicationContext.APP_DIR)
                .url(downloadable.getBaseUrl())
                .buildAsList();

        return runCommand(command);
    }

    public void checkYoutubeDlUpdates() throws IOException, InterruptedException {
        runCommand(YoutubeDlCommandBuilder.newInstance().removeCache().buildAsList()).waitFor();
        runCommand(YoutubeDlCommandBuilder.newInstance().update().buildAsList()).waitFor();
    }
}
