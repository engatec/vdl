package com.github.engatec.vdl.worker.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.DownloadManager;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.worker.data.QueueItemDownloadProgressData;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueueItemDownloadService extends Service<QueueItemDownloadProgressData> {

    private static final Logger LOGGER = LogManager.getLogger(QueueItemDownloadService.class);

    private static final String GROUP_PROGRESS = "progress";
    private static final String GROUP_SIZE = "size";
    private static final String GROUP_THROUGHPUT = "throughput";

    private static final Pattern DOWNLOAD_PROGRESS_PATTERN = Pattern.compile(
            "\\s*\\[download]\\s+" +
                    "(?<progress>\\d+\\.?\\d*)%\\s+" +
                    "of\\s+(?<size>.+)\\s+" +
                    "at\\s+(?<throughput>.+/s)\\s+" +
                    ".*"
    );

    private final QueueItem queueItem;

    public QueueItemDownloadService(QueueItem queueItem) {
        this.queueItem = queueItem;
        setExecutor(ApplicationContext.INSTANCE.getExecutorService());
    }

    @Override
    public void start() {
        DownloadStatus status = queueItem.getStatus();
        if (status != DownloadStatus.READY) {
            String msg = "Queue item must be in state " + DownloadStatus.READY + ". Was in state " + status;
            LOGGER.error(msg);
            throw new IllegalStateException(msg);
        }
        queueItem.setStatus(DownloadStatus.SCHEDULED);

        bindValueProperty();
        super.start();
    }

    @Override
    public void restart() {
        DownloadStatus status = queueItem.getStatus();
        if (status != DownloadStatus.CANCELLED) {
            String msg = "Queue item must be in state " + DownloadStatus.CANCELLED + ". Was in state " + status;
            LOGGER.error(msg);
            throw new IllegalStateException(msg);
        }
        queueItem.setStatus(DownloadStatus.READY);

        super.restart();
    }

    @Override
    protected void running() {
        queueItem.setStatus(DownloadStatus.IN_PROGRESS);
    }

    @Override
    protected void succeeded() {
        updateQueueItem(DownloadStatus.FINISHED, 1, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Override
    protected void cancelled() {
        updateQueueItem(DownloadStatus.CANCELLED, Double.NaN, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Override
    protected void failed() {
        updateQueueItem(DownloadStatus.FAILED, 0, null, StringUtils.EMPTY);
        Throwable ex = getException();
        LOGGER.error(ex.getMessage(), ex);
    }

    private void bindValueProperty() {
        valueProperty().addListener((observable, oldValue, newValue) -> updateQueueItem(null, newValue.getProgress() / 100, newValue.getSize(), newValue.getThroughput()));
    }

    private void updateQueueItem(DownloadStatus status, double progress, String size, String throughput) {
        if (status != null) {
            queueItem.setStatus(status);
        }

        if (size != null) {
            queueItem.setSize(size);
        }

        if (!Double.isNaN(progress)) {
            queueItem.setProgress(progress);
        }

        queueItem.setThroughput(throughput);
    }

    @Override
    protected Task<QueueItemDownloadProgressData> createTask() {
        return new Task<>() {
            @Override
            protected QueueItemDownloadProgressData call() throws Exception {
                var progressData = new QueueItemDownloadProgressData();
                Process process = DownloadManager.INSTANCE.download(queueItem);
                try (Stream<String> lines = new BufferedReader(new InputStreamReader(process.getInputStream(), ApplicationContext.INSTANCE.getSystemEncoding())).lines()) {
                    lines.filter(StringUtils::isNotBlank).forEach(it -> {
                        if (isCancelled()) {
                            process.destroy();
                            return;
                        }

                        Matcher matcher = DOWNLOAD_PROGRESS_PATTERN.matcher(it);
                        if (matcher.matches()) {
                            double progress = Double.parseDouble(matcher.group(GROUP_PROGRESS));
                            int progressComparisonResult = Double.compare(progress, progressData.getProgress());
                            if (progressComparisonResult == 1 || progressComparisonResult == -1) {
                                var pd = new QueueItemDownloadProgressData(progress, matcher.group(GROUP_SIZE), matcher.group(GROUP_THROUGHPUT));
                                progressData.setProgress(pd.getProgress());
                                progressData.setSize(pd.getSize());
                                progressData.setThroughput(pd.getThroughput());
                                updateValue(pd);
                            }
                        }
                    });
                }
                return progressData;
            }
        };
    }
}
