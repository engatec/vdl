package com.github.engatec.vdl.model.downloadable;

import java.nio.file.Path;

public interface Downloadable {

    String getFormatId();

    String getBaseUrl();

    Path getDownloadPath();
    void setDownloadPath(Path downloadPath);
}
