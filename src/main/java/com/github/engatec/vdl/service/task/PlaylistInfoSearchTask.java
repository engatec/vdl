package com.github.engatec.vdl.service.task;

import java.util.List;

import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.util.YouDlUtils;

public class PlaylistInfoSearchTask extends VideoInfoSearchTask {

    public PlaylistInfoSearchTask(List<String> urls) {
        super(urls);
    }

    @Override
    protected List<VideoInfo> call() throws Exception {
        List<VideoInfo> foundItems = super.call();
        return foundItems.stream()
                .filter(YouDlUtils::isPlaylist)
                .toList();
    }
}
