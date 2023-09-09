package com.github.engatec.vdl.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.youtubedl.processbuilder.DownloadProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.DownloadWithConfigFileProcessBuilder;
import com.github.engatec.vdl.core.youtubedl.processbuilder.YoutubeDlProcessBuilder;
import com.github.engatec.vdl.exception.ProcessException;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.preference.property.engine.UseConfigFileConfigProperty;
import com.github.engatec.vdl.service.data.DownloadProgressData;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueueItemDownloadService extends Service<DownloadProgressData> {

    private static final Logger LOGGER = LogManager.getLogger(QueueItemDownloadService.class);

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final QueueManager queueManager = ctx.getManager(QueueManager.class);

    private static final String SIZE_SEPARATOR = " / ";

    private static final String ERROR_PREFIX = "ERROR:";

    private static final String GROUP_PROGRESS = "progress";
    private static final String GROUP_SIZE = "size";
    private static final String GROUP_THROUGHPUT = "throughput";
    private static final String GROUP_DESTINATION = "destination";

    private static final Pattern DOWNLOAD_PROGRESS_PATTERN = Pattern.compile(
            "\\s*\\[download].*?" +
                    "(?<progress>\\d+\\.?\\d*)%.*?" +
                    "of.*?(?<size>\\d+.+?)\\s+" +
                    "(in.*)?" +
                    "at\\s+(?<throughput>.+?)\\s+" +
                    ".*"
    );

    private static final Pattern DOWNLOAD_DESTINATION_PATTERN = Pattern.compile("\\s*\\[download] Destination:(?<destination>.*)");
    private static final Pattern MERGE_PATTERN = Pattern.compile("\\s*\\[(Merger|ffmpeg)] Merging formats into \"(?<destination>.*)\"");
    private static final Pattern EXTRACT_AUDIO_PATTERN = Pattern.compile("\\s*\\[(ExtractAudio|ffmpeg)] Destination:(?<destination>.*)");

    private final List<Process> processes = Collections.synchronizedList(new ArrayList<>());
    private final QueueItem queueItem;
    private static final int MAX_PROGRESS = 100;

    public QueueItemDownloadService(QueueItem queueItem) {
        super();
        this.queueItem = queueItem;
        setExecutor(ctx.appExecutors().get(AppExecutors.Type.QUEUE_EXECUTOR));

        queueItem.progressProperty().bind(progressProperty());
        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateQueueItem(null, newValue.getSize(), newValue.getThroughput());
            }
        });
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

        super.start();
    }

    @Override
    public void reset() {
        queueItem.setStatus(DownloadStatus.READY);

        DoubleProperty progressProperty = queueItem.progressProperty();
        if (!progressProperty.isBound()) {
            progressProperty.bind(progressProperty());
        }

        super.reset();
    }

    @Override
    public void restart() {
        Set<DownloadStatus> restartableStates = Set.of(DownloadStatus.STOPPED, DownloadStatus.FAILED);
        DownloadStatus status = queueItem.getStatus();
        if (!restartableStates.contains(status)) {
            String msg = String.format("Queue item must be in one of the following states: %s. Was in state %s.", restartableStates, status);
            LOGGER.error(msg);
            throw new IllegalStateException(msg);
        }

        super.restart();
    }

    @Override
    protected void running() {
        queueItem.setStatus(DownloadStatus.IN_PROGRESS);
    }

    @Override
    protected void succeeded() {
        updateQueueItem(DownloadStatus.FINISHED, null, StringUtils.EMPTY);
        updateProgress(1);
        queueManager.removeItem(queueItem);
    }

    @Override
    protected void cancelled() {
        updateQueueItem(DownloadStatus.STOPPED, StringUtils.EMPTY, StringUtils.EMPTY);
        updateProgress(0);
        terminateProcessses();
    }

    @Override
    protected void failed() {
        updateQueueItem(DownloadStatus.FAILED, null, StringUtils.EMPTY);
        updateProgress(0);
        LOGGER.error(getException().getMessage());
        terminateProcessses();
    }

    private void addProcess(Process process) {
        processes.add(process);
    }

    private void terminateProcessses() {
        for (Process p : processes) {
            p.descendants().forEach(ProcessHandle::destroy);
            p.destroy();
        }
    }

    /**
     * @return all processes created by this service
     */
    public List<Process> getProcesses() {
        synchronized (processes) {
            return List.copyOf(processes);
        }
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
    protected Task<DownloadProgressData> createTask() {
        return new Task<>() {
            /* Small performance tweak. JavaFX's updateValue() will actually perform update only if the new value and the old one aren't referencing the same object.
               Instead of creating a new object each time making a swarm of them per download I will just rollover two objects. */
            private DownloadProgressData progressDataCurrent = new DownloadProgressData();
            private DownloadProgressData progressDataPending = new DownloadProgressData();

            private void updateProgressData(double progress, String throughput, String size) {
                progressDataCurrent.setProgress(progress);
                progressDataCurrent.setThroughput(throughput);
                progressDataCurrent.setSize(size);
            }

            private DownloadProgressData getProgressData() {
                progressDataPending.setProgress(progressDataCurrent.getProgress());
                progressDataPending.setThroughput(progressDataCurrent.getThroughput());
                progressDataPending.setSize(progressDataCurrent.getSize());

                DownloadProgressData temp = progressDataCurrent;
                progressDataCurrent = progressDataPending;
                progressDataPending = temp;

                return progressDataCurrent;
            }

            @Override
            protected DownloadProgressData call() throws Exception {
                Process process = createDownloadProcess();
                addProcess(process);
                try (var reader = process.inputReader()) {
                    reader.lines().filter(StringUtils::isNotBlank).forEach(it -> {
                        if (Thread.interrupted()) {
                            cancel();
                            return;
                        }

                        Matcher progressMatcher = DOWNLOAD_PROGRESS_PATTERN.matcher(it);
                        if (progressMatcher.matches()) {
                            double currentProgress = Double.parseDouble(progressMatcher.group(GROUP_PROGRESS));
                            updateProgressData(currentProgress, progressMatcher.group(GROUP_THROUGHPUT), calculateSizeString(progressDataCurrent.getSize(), progressMatcher.group(GROUP_SIZE)));
                            updateProgress(currentProgress, MAX_PROGRESS);
                            updateValue(getProgressData());
                        } else {
                            Matcher downloadDestinationMatcher = DOWNLOAD_DESTINATION_PATTERN.matcher(it);
                            Matcher mergeMatcher = MERGE_PATTERN.matcher(it);
                            Matcher extractAudioMatcher = EXTRACT_AUDIO_PATTERN.matcher(it);
                            if (downloadDestinationMatcher.matches()) {
                                queueManager.addDestination(queueItem, downloadDestinationMatcher.group(GROUP_DESTINATION));
                                if (StringUtils.isNotBlank(progressDataCurrent.getSize())) {
                                    updateProgressData(0, progressDataCurrent.getThroughput(), progressDataCurrent.getSize() + SIZE_SEPARATOR);
                                }
                            } else if (mergeMatcher.matches()) {
                                queueManager.addDestination(queueItem, mergeMatcher.group(GROUP_DESTINATION));
                            } else if (extractAudioMatcher.matches()) {
                                queueManager.addDestination(queueItem, extractAudioMatcher.group(GROUP_DESTINATION));
                            }
                        }
                    });
                }

                try (InputStream errorStream = process.getErrorStream()) {
                    checkErrors(errorStream);
                } catch (ProcessException e) {
                    process.destroy();
                    throw e;
                }

                return null;
            }

            private Process createDownloadProcess() throws IOException {
                Boolean useConfigFile = ctx.getConfigRegistry().get(UseConfigFileConfigProperty.class).getValue();
                YoutubeDlProcessBuilder pb = useConfigFile ? new DownloadWithConfigFileProcessBuilder(queueItem) : new DownloadProcessBuilder(queueItem);
                List<String> command = pb.buildCommand();
                return pb.buildProcess(command);
            }

            /**
             * Method updates latest not finished item size
             */
            private String calculateSizeString(String currentlyDisplayed, String youtubeDlParsed) {
                String finishedItemsSize = StringUtils.substringBeforeLast(currentlyDisplayed, SIZE_SEPARATOR);
                if (Objects.equals(currentlyDisplayed, finishedItemsSize)) { // There's no yet finished item
                    return youtubeDlParsed;
                } else {
                    return finishedItemsSize + SIZE_SEPARATOR + youtubeDlParsed;
                }
            }

            private void checkErrors(InputStream errorStream) throws IOException {
                List<String> errLines = IOUtils.readLines(errorStream, ctx.getSystemCharset());
                if (CollectionUtils.isEmpty(errLines)) {
                    return;
                }

                String msg = String.join(System.lineSeparator(), errLines);
                // ErrorStream aggregates not only errors, but warnings as well. Don't care much about warnings so they will be just logged and ignored, but errors are showstoppers
                boolean hasErrors = errLines.stream()
                        .filter(StringUtils::isNotBlank)
                        .map(StringUtils::strip)
                        .anyMatch(it -> it.startsWith(ERROR_PREFIX));
                if (hasErrors) {
                    throw new ProcessException(msg);
                } else {
                    LOGGER.warn(msg);
                }
            }
        };
    }
}
