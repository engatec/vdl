package com.github.engatec.vdl.util;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.StringJoiner;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YouDlUtils {

    private static final Logger LOGGER = LogManager.getLogger(YouDlUtils.class);

    public static String createFormatByHeight(Integer height) {
        String h = StringUtils.EMPTY;
        if (height != null) {
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
        for (String path : CollectionUtils.emptyIfNull(paths)) {
            deleteTempFiles(path);
        }
    }

    public static void deleteTempFiles(String path) {
        if (StringUtils.isBlank(path)) {
            return;
        }

        String normalizedPath = StringUtils.strip(path);
        String tempFileLocation = StringUtils.appendIfMissing(normalizedPath, ".part");
        try {
            // Need to delete both (temp and normal) because often video and audio are downloaded separately
            // and one of them can be finished by the time user decided to stop and delete item from downloads queue
            Files.deleteIfExists(Path.of(tempFileLocation));
            Files.deleteIfExists(Path.of(normalizedPath));
        } catch (InvalidPathException e) {
            LOGGER.warn("Invalid path '{}'", normalizedPath);
        } catch (DirectoryNotEmptyException e) {
            LOGGER.warn("File expected, got directory instead. '{}'", normalizedPath);
        } catch (IOException e) {
            LOGGER.warn("Couldn't delete file", e);
        }
    }
}
