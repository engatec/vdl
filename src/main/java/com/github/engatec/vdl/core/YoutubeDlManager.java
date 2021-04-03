package com.github.engatec.vdl.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.core.youtubedl.processbuilder.CacheRemoveProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.DownloadProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.DownloadWithConfigFileProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.DownloadableInfoFetchProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.VersionFetchProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.YoutubeDlProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.YoutubeDlUpdateProcessBuilder;
import com.github.engatec.vdl.exception.YoutubeDlProcessException;
import com.github.engatec.vdl.model.DownloadableInfo;
import com.github.engatec.vdl.model.HistoryItem;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.preferences.wrapper.misc.HistoryEntriesNumberPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YoutubeDlManager {

    private static final Logger LOGGER = LogManager.getLogger(YoutubeDlManager.class);

    public static final YoutubeDlManager INSTANCE = new YoutubeDlManager();

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public List<DownloadableInfo> fetchDownloadableInfo(String url) throws IOException {
        var pb = new DownloadableInfoFetchProcessBuilder(url);
        List<String> command = pb.buildCommand();
        Process process = pb.buildProcess(command);
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            List<String> jsonList = reader.lines().collect(Collectors.toList());
            List<DownloadableInfo> downloadableInfoList = new ArrayList<>(jsonList.size());
            for (String json : jsonList) {
                DownloadableInfo downloadableInfo = objectMapper.readValue(json, DownloadableInfo.class);
                if (StringUtils.isBlank(downloadableInfo.getBaseUrl())) {
                    downloadableInfo.setBaseUrl(downloadableInfo.getUrl());
                }
                downloadableInfoList.add(downloadableInfo);
            }

            // Log encountered errors that didn't result in exception
            try (InputStream errorStream = process.getErrorStream()) {
                logErrors(errorStream);
            }

            return downloadableInfoList;
        } catch (Exception e) {
            try (InputStream errorStream = process.getErrorStream()) {
                logErrors(errorStream);
            }
            LOGGER.error("Failed command: '{}'", String.join(StringUtils.SPACE, command));
            throw e;
        }
    }

    private void logErrors(InputStream errorStream) {
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
        if (ConfigRegistry.get(HistoryEntriesNumberPref.class).getValue() > 0) { // TODO: Think about eventbus instead
            HistoryManager.INSTANCE.addHistoryItem(
                    new HistoryItem(downloadable.getTitle(), downloadable.getBaseUrl(), downloadable.getDownloadPath(), LocalDateTime.now().format(dateTimeFormatter))
            );
        }

        Boolean useConfigFile = ConfigRegistry.get(UseConfigFilePref.class).getValue();
        YoutubeDlProcessBuilder pb = useConfigFile ? new DownloadWithConfigFileProcessBuilder(downloadable) : new DownloadProcessBuilder(downloadable);
        List<String> command = pb.buildCommand();
        return pb.buildProcess(command);
    }

    public String getYoutubeDlVersion() {
        String version = null;

        try {
            var pb = new VersionFetchProcessBuilder();
            List<String> command = pb.buildCommand();
            Process process = pb.buildProcess(command);
            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                version = reader.lines().findFirst().orElseThrow(YoutubeDlProcessException::new);
            } catch (Exception e) {
                try (InputStream errorStream = process.getErrorStream()) {
                    logErrors(errorStream);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return version;
    }

    public void checkYoutubeDlUpdates() throws IOException, InterruptedException {
        List<YoutubeDlProcessBuilder> processBuilders = List.of(new CacheRemoveProcessBuilder(), new YoutubeDlUpdateProcessBuilder());
        for (YoutubeDlProcessBuilder pb : processBuilders) {
            List<String> command = pb.buildCommand();
            Process process = pb.buildProcess(command);
            process.waitFor();
        }
    }
}
