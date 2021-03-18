package com.github.engatec.vdl.core.youtubedl.processbuilder;

import java.io.IOException;
import java.util.List;

public interface YoutubeDlProcessBuilder {

    List<String> buildCommand();

    Process buildProcess(List<String> command) throws IOException;
}
