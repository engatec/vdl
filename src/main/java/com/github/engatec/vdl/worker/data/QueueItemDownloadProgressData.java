package com.github.engatec.vdl.worker.data;

public class QueueItemDownloadProgressData {

    private double progress;
    private String size;
    private String throughput;

    public QueueItemDownloadProgressData() {
    }

    public QueueItemDownloadProgressData(double progress, String size, String throughput) {
        this.progress = progress;
        this.size = size;
        this.throughput = throughput;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getThroughput() {
        return throughput;
    }

    public void setThroughput(String throughput) {
        this.throughput = throughput;
    }
}
