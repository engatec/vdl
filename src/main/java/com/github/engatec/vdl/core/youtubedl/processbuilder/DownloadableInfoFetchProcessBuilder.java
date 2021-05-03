package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.util.List;

import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandHelper;
import org.apache.commons.collections4.CollectionUtils;

public class DownloadableInfoFetchProcessBuilder implements YoutubeDlProcessBuilder {

    private final List<String> urls;

    public DownloadableInfoFetchProcessBuilder(List<String> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            throw new IllegalArgumentException("urls must not be empty");
        }
        this.urls = urls;
    }

    @Override
    public List<String> buildCommand() {
        YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance()
                .dumpJson()
                .ignoreErrors()
                .noCheckCertificate()
                .flatPlaylist();

        YoutubeDlCommandHelper.setNetworkOptions(commandBuilder);
        YoutubeDlCommandHelper.setAuthenticationOptions(commandBuilder);

        return commandBuilder
                .urls(urls)
                .buildAsList();
    }

    @Override
    public Process buildProcess(List<String> command) throws IOException {
        return new ProcessBuilder(command).start();
    }
}
