package com.github.engatec.vdl.model.downloadable;

import java.nio.file.Path;

public abstract class BaseDownloadable implements Downloadable {

    protected String baseUrl;
    protected Path downloadPath;

    @Override
    public String getBaseUrl() {
        return baseUrl;
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
