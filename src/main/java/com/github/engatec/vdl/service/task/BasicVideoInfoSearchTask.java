package com.github.engatec.vdl.service.task;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.engatec.vdl.model.VideoInfo;

public class BasicVideoInfoSearchTask extends VideoInfoSearchTask {

    public BasicVideoInfoSearchTask(List<String> urls) {
        super(urls);
    }

    @Override
    protected List<VideoInfo> call() throws Exception {
        List<VideoInfo> foundItems = super.call();
        return foundItems.stream()
                .filter(Predicate.not(super::isCompleteVideoInfo))
                .collect(Collectors.toList());
    }
}
