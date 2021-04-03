package com.github.engatec.vdl.model.downloadable;

import org.apache.commons.lang3.StringUtils;

public class CustomFormatDownloadable extends BaseDownloadable {

    private String formatId;

    public CustomFormatDownloadable(String baseUrl, String formatId) {
        super(StringUtils.EMPTY, baseUrl);
        this.formatId = formatId;
    }

    @Override
    public String getFormatId() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }
}
