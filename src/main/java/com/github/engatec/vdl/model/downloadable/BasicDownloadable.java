package com.github.engatec.vdl.model.downloadable;

import java.nio.file.Path;

public class BasicDownloadable implements Downloadable {

    private String baseUrl;
    private Path downloadPath;
    private String formatId;

    public BasicDownloadable(String baseUrl, String formatId) {
        this.baseUrl = baseUrl;
        this.formatId = formatId;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getFormatId() {
        return formatId;
    }

    @Override
    public Path getDownloadPath() {
        return downloadPath;
    }

    @Override
    public void setDownloadPath(Path downloadPath) {
        this.downloadPath = downloadPath;
    }
}
