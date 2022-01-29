package com.github.engatec.vdl.core.youtubedl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.model.preferences.wrapper.misc.DownloaderPref;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class YoutubeDlCommandBuilder {

    private List<String> commandList;

    private YoutubeDlCommandBuilder() {
    }

    public static YoutubeDlCommandBuilder newInstance() {
        Engine engine = Engine.getByConfigValue(ApplicationContext.getInstance().getConfigRegistry().get(DownloaderPref.class).getValue());
        return newInstance(engine);
    }

    public static YoutubeDlCommandBuilder newInstance(Engine engine) {
        var o = new YoutubeDlCommandBuilder();
        o.commandList = new ArrayList<>();
        o.commandList.add(ApplicationContext.getInstance().getDownloaderPath(engine).toString());
        if (engine == Engine.YT_DLP) {
            o.commandList.add("--compat-options");
            o.commandList.add("all");
        }
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
    public YoutubeDlCommandBuilder urls(List<String> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            throw new IllegalArgumentException("urls must not be empty");
        }
        commandList.add("--"); // Telling the shell that the command options are over and further only urls or ids go (it fixes the issue when youtube video id starts with dash)
        commandList.addAll(urls);
        return this;
    }


    /* General options */
    public YoutubeDlCommandBuilder version() {
        commandList.add("--version");
        return this;
    }

    public YoutubeDlCommandBuilder update() {
        commandList.add("-U");
        return this;
    }

    public YoutubeDlCommandBuilder ignoreErrors() {
        commandList.add("-i");
        return this;
    }

    public YoutubeDlCommandBuilder ignoreConfig() {
        commandList.add("--ignore-config");
        return this;
    }

    public YoutubeDlCommandBuilder configLocation(String path) {
        commandList.add("--config-location");
        commandList.add(path);
        return this;
    }

    public YoutubeDlCommandBuilder flatPlaylist() {
        commandList.add("--flat-playlist");
        return this;
    }

    /* Network options */
    public YoutubeDlCommandBuilder proxy(String url) {
        commandList.add("--proxy");
        commandList.add(url);
        return this;
    }

    public YoutubeDlCommandBuilder socketTimeout(int seconds) {
        commandList.add("--socket-timeout");
        commandList.add(String.valueOf(seconds));
        return this;
    }

    public YoutubeDlCommandBuilder sourceAddress(String ip) {
        commandList.add("--source-address");
        commandList.add(ip);
        return this;
    }

    public YoutubeDlCommandBuilder forceIpV4() {
        commandList.add("--force-ipv4");
        return this;
    }

    public YoutubeDlCommandBuilder forceIpV6() {
        commandList.add("--force-ipv6");
        return this;
    }

    /* Download Options */
    public YoutubeDlCommandBuilder rateLimit(String limit) {
        commandList.add("-r");
        commandList.add(limit);
        return this;
    }

    public YoutubeDlCommandBuilder skipDownload() {
        commandList.add("--skip-download");
        return this;
    }

    /* Subtitles Options */
    public YoutubeDlCommandBuilder writeSub(Set<String> languages) {
        commandList.add("--write-sub");
        if (CollectionUtils.isNotEmpty(languages)) {
            commandList.add("--sub-lang");
            commandList.add(String.join(",", languages));
        } else {
            commandList.add("--all-subs");
        }
        return this;
    }

    public YoutubeDlCommandBuilder embedSub() {
        commandList.add("--embed-subs");
        return this;
    }

    public YoutubeDlCommandBuilder convertSub(String format) {
        if (StringUtils.isBlank(format)) {
            throw new IllegalArgumentException("Subtitles format must not be blank");
        }
        commandList.add("--convert-subs");
        commandList.add(format);
        return this;
    }

    /* Authentication Options */
    public YoutubeDlCommandBuilder username(String username) {
        commandList.add("-u");
        commandList.add(username);
        return this;
    }

    public YoutubeDlCommandBuilder password(String password) {
        commandList.add("-p");
        commandList.add(password);
        return this;
    }

    public YoutubeDlCommandBuilder twoFactor(String code) {
        commandList.add("-2");
        commandList.add(code);
        return this;
    }

    public YoutubeDlCommandBuilder useNetrc() {
        commandList.add("--netrc");
        return this;
    }

    public YoutubeDlCommandBuilder videoPassword(String videoPassword) {
        commandList.add("--video-password");
        commandList.add(videoPassword);
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

    public YoutubeDlCommandBuilder outputPath(String path) {
        if (StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("path must not be blank");
        }
        commandList.add("-o");
        commandList.add(path);
        return this;
    }

    public YoutubeDlCommandBuilder removeCache() {
        commandList.add("--rm-cache-dir");
        return this;
    }

    public YoutubeDlCommandBuilder markWatched() {
        commandList.add("--mark-watched");
        return this;
    }

    public YoutubeDlCommandBuilder noContinue() {
        commandList.add("--no-continue");
        return this;
    }

    public YoutubeDlCommandBuilder noPart() {
        commandList.add("--no-part");
        return this;
    }

    public YoutubeDlCommandBuilder noMTime() {
        commandList.add("--no-mtime");
        return this;
    }

    public YoutubeDlCommandBuilder cookiesFile(Path path) {
        Objects.requireNonNull(path, "path must not be null");
        commandList.add("--cookies");
        commandList.add(path.toString());
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
