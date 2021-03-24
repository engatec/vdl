package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandHelper;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;

public class DownloadProcessBuilder implements YoutubeDlProcessBuilder {

    private final Downloadable downloadable;

    public DownloadProcessBuilder(Downloadable downloadable) {
        Objects.requireNonNull(downloadable);
        this.downloadable = downloadable;
    }

    @Override
    public List<String> buildCommand() {
        YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();

        commandBuilder
                .formatId(downloadable.getFormatId())
                .outputPath(downloadable.getDownloadPath(), downloadable.getTitle())
                .ignoreConfig()
                .ignoreErrors()
                .noCheckCertificate()
                .ffmpegLocation(ApplicationContext.APP_DIR);

        YoutubeDlCommandHelper.setNetworkOptions(commandBuilder);
        YoutubeDlCommandHelper.setAuthenticationOptions(commandBuilder);

        if (ConfigRegistry.get(NoMTimePref.class).getValue()) {
            commandBuilder.noMTime();
        }

        for (Postprocessing pp : downloadable.getPostprocessingSteps()) {
            commandBuilder.addAll(pp.getCommandList());
        }

        return commandBuilder
                .url(downloadable.getBaseUrl())
                .buildAsList();
    }

    @Override
    public Process buildProcess(List<String> command) throws IOException {
        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
    }
}
