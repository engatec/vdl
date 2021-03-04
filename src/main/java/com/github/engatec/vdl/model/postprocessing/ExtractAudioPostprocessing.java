package com.github.engatec.vdl.model.postprocessing;

import java.util.List;

public class ExtractAudioPostprocessing implements Postprocessing {

    private String format;
    private int quality;

    private ExtractAudioPostprocessing() {
    }

    public static ExtractAudioPostprocessing newInstance(String format, int quality) {
        var instance = new ExtractAudioPostprocessing();
        instance.format = format;
        instance.quality = quality;
        return instance;
    }

    public String getFormat() {
        return format;
    }

    public int getQuality() {
        return quality;
    }

    @Override
    public List<String> getCommandList() {
        return List.of(
                "-x",
                "--audio-format", format,
                "--audio-quality", String.valueOf(quality)
        );
    }
}
