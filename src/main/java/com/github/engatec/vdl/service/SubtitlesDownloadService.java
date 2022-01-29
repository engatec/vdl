package com.github.engatec.vdl.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubtitlesDownloadService extends Service<Void> {

    private static final Logger LOGGER = LogManager.getLogger(SubtitlesDownloadService.class);

    private static final String GROUP_DESTINATION = "destination";
    private static final Pattern DOWNLOAD_DESTINATION_PATTERN = Pattern.compile("\\s*\\[info] Writing video subtitles to:(?<destination>.*)");

    private final String url;
    private final Path downloadPath;

    public SubtitlesDownloadService(String url, Path downloadPath) {
        this.url = url;
        this.downloadPath = downloadPath;
    }

    @Override
    protected void failed() {
        Throwable e = getException();
        if (e != null) {
            LOGGER.error(e.getMessage(), e);
        }
        Dialogs.error("subtitles.download.error");
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<String> command = YoutubeDlCommandBuilder.newInstance()
                        .outputPath(FilenameUtils.removeExtension(downloadPath.toString()))
                        .writeSub(Set.of())
                        .convertSub("srt")
                        .skipDownload()
                        .urls(List.of(url))
                        .buildAsList();

                List<String> destinations = new ArrayList<>();
                Process downloadProcess = new ProcessBuilder().command(command).redirectErrorStream(true).start();
                try (var reader = new BufferedReader(new InputStreamReader(downloadProcess.getInputStream()))) {
                    reader.lines()
                            .filter(StringUtils::isNotBlank)
                            .peek(LOGGER::info)
                            .forEach(it -> {
                                Matcher destinationMatcher = DOWNLOAD_DESTINATION_PATTERN.matcher(it);
                                if (destinationMatcher.matches()) {
                                    String destination = StringUtils.strip(destinationMatcher.group(GROUP_DESTINATION));
                                    destinations.add(destination);
                                }
                            });
                }

                String ffmpeg = ApplicationContext.getInstance().getAppBinariesDir().resolve("ffmpeg").toString();
                for (String dest : destinations) {
                    Path destinationPath = Path.of(dest);
                    if (Files.exists(destinationPath) && !"srt".equalsIgnoreCase(FilenameUtils.getExtension(dest))) {
                        Process convertProcess = new ProcessBuilder().command(List.of(ffmpeg, "-y", "-i", dest, FilenameUtils.removeExtension(dest) + ".srt"))
                                .redirectErrorStream(true) // ffmpeg for some reason writes info to ErrorStream, so merge it with standart output stream
                                .start();
                        convertProcess.onExit().thenAccept(process -> {
                            if (process.exitValue() == 0) { // delete original files only if conversion was successful
                                try {
                                    Files.deleteIfExists(destinationPath);
                                } catch (IOException e) {
                                    LOGGER.warn(e.getMessage(), e);
                                }
                            }
                        });
                        if (LOGGER.isInfoEnabled()) {
                            try (var reader = new BufferedReader(new InputStreamReader(convertProcess.getInputStream()))) {
                                reader.lines().filter(StringUtils::isNotBlank).forEach(LOGGER::info);
                            }
                        }
                    }
                }

                return null;
            }
        };
    }
}
