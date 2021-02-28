package com.github.engatec.vdl.core.command;

import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.model.downloadable.Downloadable;

public class EnqueueCommand implements Command {

    private final Downloadable downloadable;

    public EnqueueCommand(Downloadable downloadable) {
        this.downloadable = downloadable;
    }

    @Override
    public void execute() {
        var item = new QueueItem();
        item.setBaseUrl(downloadable.getBaseUrl());
        item.setFormatId(downloadable.getFormatId());
        item.setDownloadPath(downloadable.getDownloadPath());
        item.setPostprocessingSteps(downloadable.getPostprocessingSteps());
        QueueManager.INSTANCE.addItem(item);
    }
}
