package com.github.engatec.vdl.model.downloadable;

import java.nio.file.Path;

import com.github.engatec.vdl.model.postprocessing.Postprocessable;

public interface Downloadable extends Postprocessable {

    String getFormatId();
    void setFormatId(String formatId);

    String getTitle();
    void setTitle(String title);

    String getBaseUrl();
    void setBaseUrl(String baseUrl);

    Path getDownloadPath();
    void setDownloadPath(Path downloadPath);
}
