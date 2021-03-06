package com.github.engatec.vdl.core.youtubedl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.engatec.vdl.core.ApplicationContext;
import org.apache.commons.lang3.StringUtils;

public class YoutubeDlCommandBuilder {

    private final int MAX_TITLE_LENGTH = 200;

    private List<String> commandList;

    private YoutubeDlCommandBuilder() {
    }

    public static YoutubeDlCommandBuilder newInstance() {
        var o = new YoutubeDlCommandBuilder();
        o.commandList = new ArrayList<>();
        o.commandList.add(ApplicationContext.INSTANCE.getYoutubeDlPath().toString());
        return o;
    }

    public String build() {
        return String.join(StringUtils.SPACE, buildAsList());
    }

    public List<String> buildAsList() {
        return List.copyOf(commandList);
    }

    /**
     * If present - must be the latest before .build()
     */
    public YoutubeDlCommandBuilder url(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url must not be blank");
        }
        commandList.add(url);
        return this;
    }

    public YoutubeDlCommandBuilder ffmpegLocation(String location) {
        if (StringUtils.isBlank(location)) {
            return this;
        }

        commandList.add("--ffmpeg-location");
        commandList.add(location);
        return this;
    }

    public YoutubeDlCommandBuilder noDebug() {
        commandList.add("--no-call-home");
        return this;
    }

    public YoutubeDlCommandBuilder dumpJson() {
        commandList.add("-j");
        return this;
    }

    public YoutubeDlCommandBuilder formatId(String formatId) {
        if (StringUtils.isBlank(formatId)) {
            throw new IllegalArgumentException("formatId must not be blank");
        }
        commandList.add("-f");
        commandList.add(formatId);
        return this;
    }

    public YoutubeDlCommandBuilder outputPath(Path path, String title) {
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }

        title = StringUtils.length(title) > MAX_TITLE_LENGTH ? "%(id)s" : "%(title)s";

        commandList.add("-o");
        commandList.add(path.resolve(title + ".%(ext)s").toString());
        return this;
    }

    public YoutubeDlCommandBuilder removeCache() {
        commandList.add("--rm-cache-dir");
        return this;
    }

    public YoutubeDlCommandBuilder update() {
        commandList.add("-U");
        return this;
    }

    public YoutubeDlCommandBuilder version() {
        commandList.add("--version");
        return this;
    }

    public YoutubeDlCommandBuilder noMTime() {
        commandList.add("--no-mtime");
        return this;
    }

    public YoutubeDlCommandBuilder configLocation(String path) {
        commandList.add("--config-location");
        commandList.add(path);
        return this;
    }

    public YoutubeDlCommandBuilder ignoreConfig() {
        commandList.add("--ignore-config");
        return this;
    }

    public YoutubeDlCommandBuilder ignoreErrors() {
        commandList.add("-i");
        return this;
    }

    public YoutubeDlCommandBuilder noCheckCertificate() {
        commandList.add("--no-check-certificate");
        return this;
    }

    public YoutubeDlCommandBuilder addAll(List<String> commands) {
        commandList.addAll(commands);
        return this;
    }
}
