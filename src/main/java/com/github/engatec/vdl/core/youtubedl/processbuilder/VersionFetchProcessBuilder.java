package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.util.List;

import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlCommandBuilder;

public class VersionFetchProcessBuilder implements YoutubeDlProcessBuilder {

    private final Engine engine;

    public VersionFetchProcessBuilder(Engine engine) {
        this.engine = engine;
    }

    @Override
    public List<String> buildCommand() {
        return YoutubeDlCommandBuilder.newInstance(engine).version().buildAsList();
    }

    @Override
    public Process buildProcess(List<String> command) throws IOException {
        return new ProcessBuilder(command).start();
    }
}
