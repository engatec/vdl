package com.github.engatec.vdl.util;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.preference.property.youtubedl.WriteSubtitlesConfigProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YouDlUtils {

    private static final Logger LOGGER = LogManager.getLogger(YouDlUtils.class);

    private static final Set<String> playlistExtractors = Set.of(
            "youtube:playlist", "youtube:tab", "youtube:watchlater"
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

    public static void deleteTempFiles(Collection<? extends String> paths) {
        Boolean subtitlesEnabled = ApplicationContext.getInstance().getConfigRegistry().get(WriteSubtitlesConfigProperty.class).getValue();
        for (String path : CollectionUtils.emptyIfNull(paths)) {
            deleteTempFiles(path, 0);
            if (subtitlesEnabled) {
                deleteSubtitles(path);
            }
        }
    }

    public static void deleteTempFiles(String path, int attempt) {
        if (StringUtils.isBlank(path) || attempt > 5) {
            return;
        }

        String normalizedPath = StringUtils.strip(path);
        String partFilePath = StringUtils.appendIfMissing(normalizedPath, ".part");
        try {
            // Try to delete both (temp and normal) as the download can be finished by the time this code executes.
            // Also need to check if deletion actually happened. If not there's a chance that the filename contains rubbish symbols and special treatment is required.
            boolean deleted = Files.deleteIfExists(Path.of(partFilePath)) || Files.deleteIfExists(Path.of(normalizedPath));
            if (!deleted) {
                String regex = "[^A-Za-zА-Яа-я0-9.]";
                Path tempFilePath = Path.of(normalizedPath);
                String normalizedNameNoRubbishSymbols = tempFilePath.getFileName().toString().replaceAll(regex, StringUtils.EMPTY);
                String partFileNameNoRubbishSymbols = StringUtils.appendIfMissing(normalizedNameNoRubbishSymbols, ".part");
                try (Stream<Path> files = Files.list(tempFilePath.getParent())) {
                    Optional<Path> foundTempFileOptional = files
                            .filter(Files::isRegularFile)
                            .filter(it -> {
                                String s = it.getFileName().toString().replaceAll(regex, StringUtils.EMPTY);
                                return s.equals(normalizedNameNoRubbishSymbols) || s.equals(partFileNameNoRubbishSymbols);
                            })
                            .findFirst();
                    if (foundTempFileOptional.isPresent()) {
                        Files.deleteIfExists(foundTempFileOptional.get());
                    }
                }
            }
        } catch (InvalidPathException e) {
            LOGGER.warn("Invalid path '{}'", normalizedPath);
        } catch (DirectoryNotEmptyException e) {
            LOGGER.warn("File expected, got directory instead. '{}'", normalizedPath);
        } catch (IOException e) {
            LOGGER.warn("Couldn't delete file '{}', retrying in 1 second", path);
            try { // In some cases file is not released by the system even if the process using it is terminated. Do a few attempts to wait and delete again
                TimeUnit.SECONDS.sleep(1);
                deleteTempFiles(path, attempt + 1);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void deleteSubtitles(String path) {
        if (StringUtils.isBlank(path)) {
            return;
        }

        try {
            Path normalizedPath = Path.of(StringUtils.strip(path));
            boolean deleted = Files.deleteIfExists(normalizedPath);
            if (!deleted) {
                Path convertedPath = Path.of(FilenameUtils.removeExtension(StringUtils.strip(path)) + ".srt");
                Files.deleteIfExists(convertedPath);
            }
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }
}
