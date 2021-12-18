package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.util.List;

import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandHelper;

public class YoutubeDlUpdateProcessBuilder implements YoutubeDlProcessBuilder {

    private final Engine engine;

    public YoutubeDlUpdateProcessBuilder(Engine engine) {
        this.engine = engine;
    }

    @Override
    public List<String> buildCommand() {
        YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance(engine)
                .noCheckCertificate()
                .update();
        YoutubeDlCommandHelper.setNetworkOptions(commandBuilder);
        return commandBuilder.buildAsList();
    }

    @Override
    public Process buildProcess(List<String> command) throws IOException {
        return new ProcessBuilder(command).start();
    }
}
