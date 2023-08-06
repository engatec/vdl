package com.github.engatec.vdl.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.preference.property.youtubedl.WriteSubtitlesConfigProperty;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YouDlUtils {

    private static final Logger LOGGER = LogManager.getLogger(YouDlUtils.class);

    private static final String GROUP_DOWNLOAD_ID = "downloadId";
    private static final Pattern PATH_PATTERN = Pattern.compile(".*(?<downloadId>DID\\d{4}\\d{6}_).*");

    private static final Set<String> playlistExtractors = Set.of(
            "youtube:favorites", "youtube:history", "youtube:playlist", "youtube:recommended", "youtube:subscriptions", "youtube:tab", "youtube:watchlater"
    );

    public static boolean isPlaylist(VideoInfo videoInfo) {
        return playlistExtractors.contains(videoInfo.extractor());
    }

    public static String createFormatByHeight(Integer height) {
        String h = StringUtils.EMPTY;
        if (height != null && height > 0) {
            h = "[height<=" + height + "]";
        }

        return new StringJoiner("/")
                .add("bestvideo" + h + "[ext=mp4]+bestaudio[ext=m4a]")
                .add("bestvideo" + h + "[ext=webm]+bestaudio[ext=webm]")
                .add("bestvideo" + h + "+bestaudio")
                .add("best" + h)
                .toString();
    }

    public static void deleteTempFiles(Path downloadPath, String downloadId) {
        deleteTempFiles(downloadPath, downloadId, 0);
        Boolean subtitlesEnabled = ApplicationContext.getInstance().getConfigRegistry().get(WriteSubtitlesConfigProperty.class).getValue();
        if (subtitlesEnabled) {
            deleteSubtitles(downloadId);
        }
    }

    private static void deleteTempFiles(Path downloadPath, String downloadId, int attempt) {
        if (StringUtils.isBlank(downloadId) || attempt > 5) {
            return;
        }

        try (Stream<Path> files = Files.list(downloadPath)) {
            files.filter(Files::isRegularFile)
                    .filter(it -> it.getFileName().toString().startsWith(downloadId))
                    .forEach(currentPath -> {
                        try {
                            Files.deleteIfExists(currentPath);
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
        } catch (IOException | UncheckedIOException e) {
            LOGGER.warn("Couldn't delete file with download id '{}' because of error '{}', retrying in 1 second", downloadId, e.getMessage());
            try { // In some cases file is not released by the system even if the process using it is terminated. Do a few attempts to wait and delete again
                TimeUnit.SECONDS.sleep(1);
                deleteTempFiles(downloadPath, downloadId, attempt + 1);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void deleteSubtitles(String downloadId) {
        if (StringUtils.isBlank(downloadId)) {
            return;
        }

        try {
            Path normalizedPath = Path.of(StringUtils.strip(downloadId));
            boolean deleted = Files.deleteIfExists(normalizedPath);
            if (!deleted) {
                Path convertedPath = Path.of(FilenameUtils.removeExtension(StringUtils.strip(downloadId)) + ".srt");
                Files.deleteIfExists(convertedPath);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    public static String updateOutputTemplateWithDownloadId(String outputTemplate) {
        LocalDate today = LocalDate.now();
        String day = StringUtils.leftPad(String.valueOf(today.getDayOfMonth()), 2, '0');
        String month = StringUtils.leftPad(String.valueOf(today.getMonthValue()), 2, '0');
        String rNum = StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(0, 1000000)), 6, '0');
        String prefix = "DID" + day + month + rNum + "_";
        return prefix + outputTemplate;
    }

    public static String extractDownloadId(String path) {
        Matcher matcher = PATH_PATTERN.matcher(path);
        return matcher.matches() ? matcher.group(GROUP_DOWNLOAD_ID) : null;
    }

    public static Optional<Path> normalizePath(Path downloadPath, String downloadId) {
        List<Path> normalizedPaths = List.of();
        try (Stream<Path> files = Files.list(downloadPath)) {
            normalizedPaths = files
                    .filter(Files::isRegularFile)
                    .filter(it -> it.getFileName().toString().startsWith(downloadId))
                    .map(currentPath -> {
                        String normalizedFilename = StringUtils.substringAfter(currentPath.getFileName().toString(), downloadId);
                        try {
                            return Files.move(currentPath, currentPath.resolveSibling(normalizedFilename));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException | UncheckedIOException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        if (normalizedPaths.isEmpty()) {
            LOGGER.warn("No file was renamed in path '{}' for downloadId '{}'", downloadPath, downloadId);
        }

        if (normalizedPaths.size() > 1) {
            LOGGER.warn("Multiple files were renamed in path '{}' for downloadId '{}'", downloadPath, downloadId);
        }

        return normalizedPaths.stream()
                .max(Comparator.comparing(it -> it.toFile().length()));
    }
}
