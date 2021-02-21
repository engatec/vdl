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
    private static final int MAX_PROGRESS_PER_ITEM = 100;
    private final int maxOverallProgress;
    private int progressModificator = 0;

    public QueueItemDownloadService(QueueItem queueItem) {
        this.queueItem = queueItem;
        maxOverallProgress = MAX_PROGRESS_PER_ITEM * (StringUtils.countMatches(queueItem.getFormatId(), '+') + 1);
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

        queueItem.progressProperty().bind(progressProperty());
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
        updateQueueItem(DownloadStatus.FINISHED, StringUtils.EMPTY, StringUtils.EMPTY);
        updateProgress(1);
    }

    @Override
    protected void cancelled() {
        updateQueueItem(DownloadStatus.CANCELLED, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    @Override
    protected void failed() {
        updateQueueItem(DownloadStatus.FAILED, null, StringUtils.EMPTY);
        updateProgress(0);
        Throwable ex = getException();
        LOGGER.error(ex.getMessage(), ex);
    }

    private void bindValueProperty() {
        valueProperty().addListener((observable, oldValue, newValue) -> updateQueueItem(null, newValue.getSize(), newValue.getThroughput()));
    }

    private void updateQueueItem(DownloadStatus status, String size, String throughput) {
        if (status != null) {
            queueItem.setStatus(status);
        }

        if (size != null) {
            queueItem.setSize(size);
        }

        queueItem.setThroughput(throughput);
    }

    private void updateProgress(double value) {
        queueItem.progressProperty().unbind();
        queueItem.setProgress(value);
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
                            double currentProgress = Double.parseDouble(matcher.group(GROUP_PROGRESS));
                            int progressComparisonResult = Double.compare(currentProgress, progressData.getProgress());
                            if (progressComparisonResult == 1) {
                                var pd = new QueueItemDownloadProgressData(currentProgress, matcher.group(GROUP_SIZE), matcher.group(GROUP_THROUGHPUT));
                                updateProgress(currentProgress + progressModificator, maxOverallProgress);
                                progressData.setProgress(pd.getProgress());
                                progressData.setSize(pd.getSize());
                                progressData.setThroughput(pd.getThroughput());
                                updateValue(pd);
                            } else if (progressComparisonResult == -1) {
                                progressModificator += MAX_PROGRESS_PER_ITEM;
                                progressData.setProgress(currentProgress);
                            }
                        }
                    });
                }
                return progressData;
            }
        };
    }
}
