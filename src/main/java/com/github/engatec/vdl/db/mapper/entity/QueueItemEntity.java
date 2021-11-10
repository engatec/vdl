package com.github.engatec.vdl.db.mapper.entity;

public class QueueItemEntity {
    private Long id;
    private String title;
    private String formatId;
    private String url;
    private String filePath;
    private String size;
    private Double progress;
    private String status;

    public QueueItemEntity() {
    }

    public QueueItemEntity(Long id,
                           String title,
                           String formatId,
                           String url,
                           String filePath,
                           String size,
                           Double progress, String status) {
        this.id = id;
        this.title = title;
        this.formatId = formatId;
        this.url = url;
        this.filePath = filePath;
        this.size = size;
        this.progress = progress;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormatId() {
        return formatId;
    }

    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
