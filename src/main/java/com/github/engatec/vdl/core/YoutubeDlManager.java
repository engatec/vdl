package com.github.engatec.vdl.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.exception.YoutubeDlProcessException;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.preferences.youtubedl.CustomArgumentsConfigItem;
import com.github.engatec.vdl.model.preferences.youtubedl.NoMTimeConfigItem;
import com.github.engatec.vdl.model.preferences.youtubedl.UseCustomArgumentsConfigItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YoutubeDlManager {

    private static final Logger LOGGER = LogManager.getLogger(YoutubeDlManager.class);

    public static final YoutubeDlManager INSTANCE = new YoutubeDlManager();

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private Process runCommand(List<String> command) throws IOException {
        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
    }

    public List<VideoInfo> fetchVideoInfo(String url) throws IOException {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url must not be blank");
        }

        List<String> command = YoutubeDlCommandBuilder.newInstance()
                .noDebug()
                .dumpJson()
                .url(url)
                .buildAsList();

        Process process = new ProcessBuilder(command).start();
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            List<String> jsonList = reader.lines().collect(Collectors.toList());
            List<VideoInfo> videoInfoList = new ArrayList<>(jsonList.size());
            for (String json : jsonList) {
                videoInfoList.add(objectMapper.readValue(json, VideoInfo.class));
            }
            return videoInfoList;
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

        InputStreamReader is;
        try {
            is = new InputStreamReader(errorStream, ApplicationContext.INSTANCE.getSystemEncoding());
        } catch (UnsupportedEncodingException e) {
            is = new InputStreamReader(errorStream);
            LOGGER.warn(e.getMessage(), e);
        }
        new BufferedReader(is).lines().forEach(LOGGER::error);
    }

    public Process download(Downloadable downloadable) throws IOException {
        ConfigManager cfg = ConfigManager.INSTANCE;

        YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();

        commandBuilder
                .noDebug()
                .formatId(downloadable.getFormatId())
                .outputPath(downloadable.getDownloadPath())
                .ffmpegLocation(ApplicationContext.APP_DIR);

        if (cfg.getValue(new NoMTimeConfigItem())) {
            commandBuilder.noMTime();
        }

        if (cfg.getValue(new UseCustomArgumentsConfigItem())) {
            String customArgumentsString = cfg.getValue(new CustomArgumentsConfigItem());
            List<String> customArguments = Arrays.stream(customArgumentsString.split("\\s+"))
                    .map(StringUtils::strip)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());
            commandBuilder.addCustomArguments(customArguments);
        }

        List<String> command = commandBuilder
                .url(downloadable.getBaseUrl())
                .buildAsList();

        return runCommand(command);
    }

    public String getYoutubeDlVersion() {
        String version = null;

        List<String> command = YoutubeDlCommandBuilder.newInstance().version().buildAsList();
        try {
            Process process = new ProcessBuilder(command).start();
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                version = reader.lines().findFirst().orElseThrow(YoutubeDlProcessException::new);
            } catch (Exception e) {
                try (InputStream errorStream = process.getErrorStream()) {
                    logError(errorStream);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return version;
    }

    public void checkYoutubeDlUpdates() throws IOException, InterruptedException {
        runCommand(YoutubeDlCommandBuilder.newInstance().removeCache().buildAsList()).waitFor();
        runCommand(YoutubeDlCommandBuilder.newInstance().update().buildAsList()).waitFor();
    }
}
