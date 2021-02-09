package com.github.engatec.vdl.core.youtubedl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class YoutubeDlCommandBuilder {

    private static final String APP_DIR = System.getProperty("app.dir");
    private static final String YOUTUBE_DL_APP_NAME = "youtube-dl";

    private String url;
    private List<String> commandList;

    private YoutubeDlCommandBuilder() {
    }

    public static YoutubeDlCommandBuilder newInstance(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url must not be blank");
        }
        var o = new YoutubeDlCommandBuilder();
        o.url = url;
        o.commandList = new ArrayList<>();
        o.commandList.add(StringUtils.defaultIfBlank(APP_DIR, StringUtils.EMPTY) + YOUTUBE_DL_APP_NAME);
        return o;
    }

    public String build() {
        return String.join(StringUtils.EMPTY, buildAsList());
    }

    public List<String> buildAsList() {
        if (StringUtils.isNotBlank(APP_DIR)) {
            commandList.add("--ffmpeg-location");
            commandList.add(APP_DIR);
        }
        commandList.add(url);
        return List.copyOf(commandList);
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
            throw new IllegalArgumentException();
        }
        commandList.add("-f");
        commandList.add(formatId);
        return this;
    }

    public YoutubeDlCommandBuilder outputPath(Path path) {
        if (path == null) {
            throw new IllegalArgumentException();
        }

        commandList.add("-o");
        commandList.add(path.resolve("%(title)s.%(ext)s").toString());
        return this;
    }
}
