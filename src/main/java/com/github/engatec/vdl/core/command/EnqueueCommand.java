package com.github.engatec.vdl.core.command;

import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.model.downloadable.BaseDownloadable;

public class EnqueueCommand implements Command {

    private final BaseDownloadable downloadable;

    public EnqueueCommand(BaseDownloadable downloadable) {
        this.downloadable = downloadable;
    }

    @Override
    public void execute() {
        QueueManager.INSTANCE.addItem(new QueueItem(downloadable));
    }
}
