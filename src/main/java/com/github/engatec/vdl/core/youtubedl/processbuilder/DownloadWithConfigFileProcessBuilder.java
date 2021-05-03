package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ConfigFilePathPref;
import org.apache.commons.lang3.StringUtils;

public class DownloadWithConfigFileProcessBuilder implements YoutubeDlProcessBuilder {

    private final Downloadable downloadable;

    public DownloadWithConfigFileProcessBuilder(Downloadable downloadable) {
        Objects.requireNonNull(downloadable);
        this.downloadable = downloadable;
    }

    @Override
    public List<String> buildCommand() {
        String configLocation = ConfigRegistry.get(ConfigFilePathPref.class).getValue();

        YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance().configLocation(configLocation);

        String configFile = StringUtils.EMPTY;
        try (Stream<String> lines = Files.lines(Path.of(configLocation))) {
            configFile = lines.map(it -> StringUtils.substringBefore(it, "#")).filter(StringUtils::isNotBlank).collect(Collectors.joining(StringUtils.SPACE));
        } catch (Exception ignored) {
            // Couldn't open config file. Youtube-dl will show the error
        }

        if (!configFile.contains("-f ") && !configFile.contains("--format ")) {
            commandBuilder.formatId(downloadable.getFormatId());
        }

        if (!configFile.contains("-o ") && !configFile.contains("--output ")) {
            commandBuilder.outputPath(downloadable.getDownloadPath(), downloadable.getTitle());
        }

        return commandBuilder
                .ffmpegLocation(ApplicationContext.APP_DIR)
                .urls(List.of(downloadable.getBaseUrl()))
                .buildAsList();
    }

    @Override
    public Process buildProcess(List<String> command) throws IOException {
        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
    }
}
