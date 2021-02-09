package com.github.engatec.vdl.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.model.Downloadable;
import com.github.engatec.vdl.model.VideoInfo;

public class DownloadManager {

    public static final DownloadManager INSTANCE = new DownloadManager();

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private Process runCommand(List<String> command) throws IOException {
        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
    }

    public VideoInfo fetchVideoInfo(String url) throws IOException {
        List<String> command = YoutubeDlCommandBuilder.newInstance(url)
                .noDebug()
                .dumpJson()
                .buildAsList();

        VideoInfo videoInfo;
        Process process = runCommand(command);
        try (InputStream is = process.getInputStream()) {
            videoInfo = objectMapper.readValue(is, VideoInfo.class);
        }
        return videoInfo;
    }

    public Process download(Downloadable downloadable, Path outputPath) throws IOException {
        List<String> command = YoutubeDlCommandBuilder.newInstance(downloadable.getBaseUrl())
                .noDebug()
                .formatId(downloadable.getFormatId())
                .outputPath(outputPath)
                .buildAsList();

        return runCommand(command);
    }
}
