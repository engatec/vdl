package com.github.engatec.vdl.service.task;

import java.util.List;

import com.github.engatec.vdl.model.VideoInfo;
import org.apache.commons.collections4.CollectionUtils;

public class PlaylistInfoSearchTask extends VideoInfoSearchTask {

    public PlaylistInfoSearchTask(List<String> urls) {
        super(urls);
    }

    @Override
    protected List<VideoInfo> call() throws Exception {
        List<VideoInfo> foundItems = super.call();
        return foundItems.stream()
                .filter(it -> CollectionUtils.isEmpty(it.formats())) // FIXME: A bit hacky solution. If formats have been found then it's not a playlist
                .toList();
    }
}
