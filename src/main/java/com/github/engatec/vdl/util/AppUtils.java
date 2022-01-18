package com.github.engatec.vdl.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.preferences.wrapper.general.AlwaysAskDownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.DownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.misc.RecentDownloadPathPref;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class AppUtils {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_SQLITE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern YOUTUBE_PATTERN = Pattern.compile(".*youtube\\.com/.*");

    public static Optional<Path> resolveDownloadPath(Stage stage) {
        Path path = doResolveDownloadPath(stage);
        if (path == null) {
            return Optional.empty();
        }

        boolean pathIsWritableDirectory = Files.isDirectory(path) && Files.isWritable(path);
        if (!pathIsWritableDirectory) {
            Dialogs.error("download.path.directory.error");
            return Optional.empty();
        }

        ApplicationContext.getInstance().getConfigRegistry().get(RecentDownloadPathPref.class).setValue(path.toString());

        return Optional.of(path);
    }

    private static Path doResolveDownloadPath(Stage stage) {
        ApplicationContext ctx = ApplicationContext.getInstance();
        ConfigRegistry configRegistry = ctx.getConfigRegistry();
        Path downloadPath = Paths.get(configRegistry.get(DownloadPathPref.class).getValue());
        boolean askPath = configRegistry.get(AlwaysAskDownloadPathPref.class).getValue();
        if (askPath) {
            var directoryChooser = new DirectoryChooser();
            File recentDownloadPath = Path.of(ctx.getConfigRegistry().get(RecentDownloadPathPref.class).getValue()).toFile();
            if (recentDownloadPath.isDirectory()) {
                directoryChooser.setInitialDirectory(recentDownloadPath);
            }
            File selectedDirectory = directoryChooser.showDialog(stage);
            downloadPath = selectedDirectory != null ? selectedDirectory.toPath() : null;
        }

        return downloadPath;
    }

    /**
     * Naive parsing youtube url to remove list if single video is expected. Should be sufficient for now.
     */
    public static String normalizeBaseUrl(String baseUrl) {
        URL url;
        try {
            url = new URL(baseUrl);
        } catch (MalformedURLException e) {
            return baseUrl;
        }

        if (!StringUtils.contains(url.getHost(), "youtube.com")) {
            return baseUrl;
        }

        if (!StringUtils.contains(url.getPath(), "/watch")) {
            return baseUrl;
        }

        String[] queryParams = StringUtils.split(url.getQuery(), '&');
        String videoIdQueryParam = Stream.of(queryParams)
                .map(StringUtils::strip)
                .filter(it -> it.startsWith("v="))
                .findFirst()
                .orElse(null);

        if (videoIdQueryParam == null) {
            return baseUrl;
        }

        return url.getProtocol() + "://" + url.getAuthority() + url.getPath() + '?' + videoIdQueryParam;
    }

    public static String normalizeThumbnailUrl(VideoInfo vi) {
        return YOUTUBE_PATTERN.matcher(vi.getBaseUrl()).matches() ? String.format("https://img.youtube.com/vi/%s/mqdefault.jpg", vi.getId()) : vi.getThumbnail();
    }

    public static String normalizeThumbnailUrlMaxRes(VideoInfo vi) {
        return YOUTUBE_PATTERN.matcher(vi.getBaseUrl()).matches() ? String.format("https://img.youtube.com/vi/%s/maxresdefault.jpg", vi.getId()) : vi.getThumbnail();
    }
}
