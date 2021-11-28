package com.github.engatec.vdl.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.preferences.wrapper.general.AlwaysAskDownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.DownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.misc.RecentDownloadPathPref;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.worker.UpdateYoutubeDlBinaryTask;
import com.github.engatec.vdl.worker.UpdateYtdlpBinaryTask;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class AppUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
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

        ApplicationContext.INSTANCE.getConfigRegistry().get(RecentDownloadPathPref.class).setValue(path.toString());

        return Optional.of(path);
    }

    private static Path doResolveDownloadPath(Stage stage) {
        ConfigRegistry configRegistry = ApplicationContext.INSTANCE.getConfigRegistry();
        Path downloadPath = Paths.get(configRegistry.get(DownloadPathPref.class).getValue());
        boolean askPath = configRegistry.get(AlwaysAskDownloadPathPref.class).getValue();
        if (askPath) {
            var directoryChooser = new DirectoryChooser();
            File recentDownloadPath = Path.of(ApplicationContext.INSTANCE.getConfigRegistry().get(RecentDownloadPathPref.class).getValue()).toFile();
            if (recentDownloadPath.isDirectory()) {
                directoryChooser.setInitialDirectory(recentDownloadPath);
            }
            File selectedDirectory = directoryChooser.showDialog(stage);
            downloadPath = selectedDirectory != null ? selectedDirectory.toPath() : null;
        }

        return downloadPath;
    }

    public static void updateYoutubeDl(Stage stage, Runnable onSuccessCallback) {
        ApplicationContext ctx = ApplicationContext.INSTANCE;
        if (!Files.isWritable(ctx.getDownloaderPath(Engine.YOUTUBE_DL))) {
            Dialogs.error("update.youtubedl.nopermissions");
            return;
        }

        Dialogs.progress("dialog.progress.title.label.updateinprogress", stage, new UpdateYoutubeDlBinaryTask(), onSuccessCallback);
    }

    public static void updateYtdlp(Stage stage, Runnable onSuccessCallback) {
        ApplicationContext ctx = ApplicationContext.INSTANCE;
        if (!Files.isWritable(ctx.getDownloaderPath(Engine.YT_DLP))) {
            Dialogs.error("update.ytdlp.nopermissions");
            return;
        }

        Dialogs.progress("dialog.progress.title.label.updateinprogress", stage, new UpdateYtdlpBinaryTask(), onSuccessCallback);
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

    public static String convertDtmToString(LocalDateTime dtm) {
        return dtm.format(DATE_TIME_FORMATTER);
    }
}
