package com.github.engatec.vdl.model.downloadable;

public class CustomFormatDownloadable extends BaseDownloadable {

    private final String formatId;

    public CustomFormatDownloadable(String baseUrl, String formatId) {
        super.baseUrl = baseUrl;
        this.formatId = formatId;
    }

    @Override
    public String getFormatId() {
        return formatId;
    }
}
