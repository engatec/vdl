package com.github.engatec.vdl.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.preference.ConfigRegistry;
import com.github.engatec.vdl.preference.property.general.AlwaysAskDownloadPathConfigProperty;
import com.github.engatec.vdl.preference.property.general.DownloadPathConfigProperty;
import com.github.engatec.vdl.preference.property.misc.RecentDownloadPathConfigProperty;
import com.github.engatec.vdl.ui.helper.Dialogs;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class AppUtils {

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

        ApplicationContext.getInstance().getConfigRegistry().get(RecentDownloadPathConfigProperty.class).setValue(path.toString());

        return Optional.of(path);
    }

    private static Path doResolveDownloadPath(Stage stage) {
        ConfigRegistry configRegistry = ApplicationContext.getInstance().getConfigRegistry();
        Path downloadPath = Paths.get(configRegistry.get(DownloadPathConfigProperty.class).getValue());
        boolean askPath = configRegistry.get(AlwaysAskDownloadPathConfigProperty.class).getValue();
        if (askPath) {
            downloadPath = choosePath(stage).orElse(null);
        }

        return downloadPath;
    }

    public static Optional<Path> choosePath(Stage stage) {
        var directoryChooser = new DirectoryChooser();
        File recentDownloadPath = Path.of(ApplicationContext.getInstance().getConfigRegistry().get(RecentDownloadPathConfigProperty.class).getValue()).toFile();
        if (recentDownloadPath.isDirectory()) {
            directoryChooser.setInitialDirectory(recentDownloadPath);
        }
        File selectedDirectory = directoryChooser.showDialog(stage);
        return Optional.ofNullable(selectedDirectory).map(File::toPath);
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
}
