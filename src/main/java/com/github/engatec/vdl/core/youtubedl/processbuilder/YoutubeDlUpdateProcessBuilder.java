package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.util.List;

import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;

public class YoutubeDlUpdateProcessBuilder implements YoutubeDlProcessBuilder {

    @Override
    public List<String> buildCommand() {
        return YoutubeDlCommandBuilder.newInstance()
                .update()
                .buildAsList();
    }

    @Override
    public Process buildProcess(List<String> command) throws IOException {
        return new ProcessBuilder(command).start();
    }
}
