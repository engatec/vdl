package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.util.List;

import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;

public class VersionFetchProcessBuilder implements YoutubeDlProcessBuilder {

    @Override
    public List<String> buildCommand() {
        return YoutubeDlCommandBuilder.newInstance()
                .version()
                .buildAsList();
    }

    @Override
    public Process buildProcess(List<String> command) throws IOException {
        return new ProcessBuilder(command).start();
    }
}
