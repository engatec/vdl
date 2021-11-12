package com.github.engatec.vdl.db.entity;

import java.nio.file.Path;

public class FindQueueTempFilesResult {

    private Long queueId;
    private Path filePath;

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }
}
