package com.github.engatec.vdl.worker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.DownloadManager;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.worker.data.QueueItemDownloadProgressData;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;

public class DownloadQueueItemTask extends Task<QueueItemDownloadProgressData> {

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

    private final Downloadable downloadable;

    public DownloadQueueItemTask(Downloadable downloadable) {
        this.downloadable =  downloadable;
    }

    @Override
    protected QueueItemDownloadProgressData call() throws Exception {
        var progressData = new QueueItemDownloadProgressData();
        Process process = DownloadManager.INSTANCE.download(downloadable);
        try (Stream<String> lines = new BufferedReader(new InputStreamReader(process.getInputStream(), ApplicationContext.INSTANCE.getSystemEncoding())).lines()) {
            lines.filter(StringUtils::isNotBlank).forEach(it -> {
                if (isCancelled()) {
                    process.destroy();
                    return;
                }

                Matcher matcher = DOWNLOAD_PROGRESS_PATTERN.matcher(it);
                if (matcher.matches()) {
                    double progress = Double.parseDouble(matcher.group(GROUP_PROGRESS));
                    // TODO: Прогресс скачивания аудио просирается
                    if (Double.compare(progress, progressData.getProgress()) == 1) {
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
}
