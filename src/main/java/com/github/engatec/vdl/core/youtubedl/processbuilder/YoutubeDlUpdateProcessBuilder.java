package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.util.List;

import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandHelper;

public class YoutubeDlUpdateProcessBuilder implements YoutubeDlProcessBuilder {

    @Override
    public List<String> buildCommand() {
        YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance().update();
        YoutubeDlCommandHelper.setNetworkOptions(commandBuilder);
        return commandBuilder.buildAsList();
    }

    @Override
    public Process buildProcess(List<String> command) throws IOException {
        return new ProcessBuilder(command).start();
    }
}
