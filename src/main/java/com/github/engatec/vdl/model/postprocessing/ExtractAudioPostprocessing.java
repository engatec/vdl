package com.github.engatec.vdl.model.postprocessing;

import java.util.List;

import com.github.engatec.vdl.model.AudioFormat;

public class ExtractAudioPostprocessing implements Postprocessing {

    private final AudioFormat format;
    private final String quality;

    public ExtractAudioPostprocessing(AudioFormat format, String quality) {
        this.format = format;
        this.quality = quality;
    }

    @Override
    public List<String> getCommandList() {
        return List.of(
                "-x",
                "--audio-format", format.toString(),
                "--audio-quality", quality
        );
    }
}
