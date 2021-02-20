package com.github.engatec.vdl.model;

import com.github.engatec.vdl.core.ApplicationContext;

public enum DownloadStatus {

    READY("stage.queue.status.ready"),
    SCHEDULED("stage.queue.status.scheduled"),
    IN_PROGRESS("stage.queue.status.inprogress"),
    CANCELLED("stage.queue.status.cancelled"),
    FAILED("stage.queue.status.failed"),
    FINISHED("stage.queue.status.finished");

    private final String msgKey;

    DownloadStatus(String msgKey) {
        this.msgKey = msgKey;
    }

    public String getMsgKey() {
        return msgKey;
    }


    @Override
    public String toString() {
        return ApplicationContext.INSTANCE.getResourceBundle().getString(msgKey);
    }
}
