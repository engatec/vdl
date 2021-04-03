package com.github.engatec.vdl.model.downloadable;

import java.nio.file.Path;
import java.util.List;

import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import org.apache.commons.collections4.ListUtils;

public abstract class BaseDownloadable implements Downloadable {

    protected String title;
    protected String baseUrl;
    protected Path downloadPath;

    protected List<Postprocessing> postprocessingSteps;

    public BaseDownloadable(String title, String baseUrl) {
        this.title = title;
        this.baseUrl = baseUrl;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Path getDownloadPath() {
        return downloadPath;
    }

    @Override
    public void setDownloadPath(Path downloadPath) {
        this.downloadPath = downloadPath;
    }

    @Override
    public List<Postprocessing> getPostprocessingSteps() {
        return ListUtils.emptyIfNull(postprocessingSteps);
    }

    @Override
    public void setPostprocessingSteps(List<Postprocessing> items) {
        postprocessingSteps = List.copyOf(ListUtils.emptyIfNull(items));
    }
}
