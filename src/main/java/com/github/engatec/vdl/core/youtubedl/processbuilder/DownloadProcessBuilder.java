package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandHelper;
import com.github.engatec.vdl.model.YoutubedlFormat;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.postprocessing.ExtractAudioPostprocessing;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;

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
                .formatId(resolveFormatId())
                .outputPath(downloadable.getDownloadPath(), downloadable.getTitle())
                .ignoreConfig()
                .ignoreErrors()
                .noCheckCertificate()
                .ffmpegLocation(ApplicationContext.APP_DIR);

        YoutubeDlCommandHelper.setGeneralOptions(commandBuilder);
        YoutubeDlCommandHelper.setNetworkOptions(commandBuilder);
        YoutubeDlCommandHelper.setAuthenticationOptions(commandBuilder);

        for (Postprocessing pp : downloadable.getPostprocessingSteps()) {
            commandBuilder.addAll(pp.getCommandList());
        }

        return commandBuilder
                .url(downloadable.getBaseUrl())
                .buildAsList();
    }

    /**
     * No need to download video if user only wants to extract audio
     */
    private String resolveFormatId() {
        boolean extractAudio = downloadable.getPostprocessingSteps().stream()
                .map(Postprocessing::getClass)
                .anyMatch(it -> it == ExtractAudioPostprocessing.class);
        return extractAudio ? YoutubedlFormat.BEST_AUDIO.getCmdValue() : downloadable.getFormatId();
    }

    @Override
    public Process buildProcess(List<String> command) throws IOException {
        return new ProcessBuilder(command)
                .redirectErrorStream(true)
                .start();
    }
}
