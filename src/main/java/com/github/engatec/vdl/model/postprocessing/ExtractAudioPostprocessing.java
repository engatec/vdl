package com.github.engatec.vdl.model.postprocessing;

import java.util.ArrayList;
import java.util.List;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.AudioFormat;
import com.github.engatec.vdl.preference.ConfigRegistry;
import com.github.engatec.vdl.preference.property.general.AudioExtractionAddMetadataConfigProperty;
import com.github.engatec.vdl.preference.property.general.AudioExtractionEmbedThumbnailConfigProperty;

public class ExtractAudioPostprocessing implements Postprocessing {

    private final AudioFormat format;
    private final String quality;

    public ExtractAudioPostprocessing(AudioFormat format, String quality) {
        this.format = format;
        this.quality = quality;
    }

    @Override
    public List<String> getCommandList() {
        var commandList = new ArrayList<String>();
        commandList.add("-x");

        commandList.add("--audio-format");
        commandList.add(format.toString());

        commandList.add("--audio-quality");
        commandList.add(quality);

        ConfigRegistry configRegistry = ApplicationContext.getInstance().getConfigRegistry();

        if (configRegistry.get(AudioExtractionAddMetadataConfigProperty.class).getValue()) {
            commandList.add("--add-metadata");
        }

        if (configRegistry.get(AudioExtractionEmbedThumbnailConfigProperty.class).getValue()) {
            commandList.add("--embed-thumbnail");
        }

        return commandList;
    }
}
