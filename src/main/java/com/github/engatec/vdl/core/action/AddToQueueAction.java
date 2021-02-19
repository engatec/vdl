package com.github.engatec.vdl.core.action;

import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.model.downloadable.Downloadable;

public class AddToQueueAction implements Action {

    private final Downloadable downloadable;

    public AddToQueueAction(Downloadable downloadable) {
        this.downloadable = downloadable;
    }

    @Override
    public void perform() {
        var item = new QueueItem();
        item.setUrl(downloadable.getBaseUrl());
        item.setFormatId(downloadable.getFormatId());
        item.setDownloadPath(downloadable.getDownloadPath());
        QueueManager.INSTANCE.addItem(item);
    }
}
