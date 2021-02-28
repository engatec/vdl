package com.github.engatec.vdl.model.downloadable;

import java.nio.file.Path;

import com.github.engatec.vdl.model.postprocessing.Postprocessable;

public interface Downloadable extends Postprocessable {

    String getFormatId();

    String getBaseUrl();

    Path getDownloadPath();
    void setDownloadPath(Path downloadPath);
}
