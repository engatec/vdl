package com.github.engatec.vdl.model.downloadable;

import com.github.engatec.vdl.model.Format;
import org.apache.commons.lang3.ObjectUtils;

public class Audio {

    private final Format format;

    public Audio(Format format) {
        this.format = format;
    }

    public String getFormatId() {
        return getId();
    }

    public String getId() {
        return format.getId();
    }

    public String getExtension() {
        return format.getExtension();
    }

    public Double getBitrate() {
        return ObjectUtils.defaultIfNull(format.getAudioBitrate(), format.getTotalBitrate());
    }

    public Long getFilesize() {
        return format.getFilesize();
    }
}
