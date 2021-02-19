package com.github.engatec.vdl.core.action;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.worker.DownloadQueueItemTask;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadQueueItemAction implements Action {

    private static final Logger LOGGER = LogManager.getLogger(DownloadQueueItemAction.class);

    private final QueueItem queueItem;

    public DownloadQueueItemAction(QueueItem queueItem) {
        this.queueItem = queueItem;
    }

    @Override
    public void perform() {
        DownloadQueueItemTask task = new DownloadQueueItemTask(queueItem);
        task.setOnRunning(e -> queueItem.setStatus(DownloadStatus.IN_PROGRESS));
        task.setOnSucceeded(e -> updateQueueItem(DownloadStatus.FINISHED, 1, StringUtils.EMPTY, StringUtils.EMPTY));
        task.setOnFailed(e -> {
            updateQueueItem(DownloadStatus.FAILED, 0, null, StringUtils.EMPTY);
            Throwable ex = e.getSource().getException();
            LOGGER.error(ex.getMessage(), ex);
        });

        task.valueProperty().addListener((observable, oldValue, newValue) -> updateQueueItem(null, newValue.getProgress() / 100, newValue.getSize(), newValue.getThroughput()));

        ApplicationContext.INSTANCE.runTaskAsync(task);
    }

    private void updateQueueItem(DownloadStatus status, double progress, String size, String throughput) {
        if (status != null) {
            queueItem.setStatus(status);
        }

        if (size != null) {
            queueItem.setSize(size);
        }

        queueItem.setProgress(progress);
        queueItem.setThroughput(throughput);
    }
}
