package com.github.engatec.vdl.worker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import org.apache.commons.lang3.StringUtils;

public class DownloadTask extends Task<Void> {

    private static final Pattern DOWNLOAD_PROGRESS_PATTERN = Pattern.compile("\\s*\\[download]\\s*\\d+\\.?\\d*%.*");

    private final TextArea downloadTextArea;
    private final Downloadable downloadable;

    public DownloadTask(Downloadable downloadable, TextArea downloadTextArea) {
        this.downloadTextArea = downloadTextArea;
        this.downloadable =  downloadable;
    }

    @Override
    protected Void call() throws Exception {
        Process process = YoutubeDlManager.INSTANCE.download(downloadable);
        try (Stream<String> lines = new BufferedReader(new InputStreamReader(process.getInputStream(), ApplicationContext.INSTANCE.getSystemEncoding())).lines()) {
            lines.filter(StringUtils::isNotBlank).forEach(it -> {
                if (isCancelled()) {
                    process.destroy();
                    return;
                }
                // Намеренно не использую здесь updateMessage, чтобы не пропустить ни одного сообщения от youtube-dl
                if (DOWNLOAD_PROGRESS_PATTERN.matcher(it).matches()) {
                    Platform.runLater(() -> {
                        int startPosition = downloadTextArea.getCaretPosition();
                        downloadTextArea.replaceText(startPosition, downloadTextArea.getLength(), it + StringUtils.LF);
                        downloadTextArea.positionCaret(startPosition);
                    });
                } else {
                    Platform.runLater(() -> {
                        int startPosition = downloadTextArea.getCaretPosition();
                        int endPosition = downloadTextArea.getLength();
                        if (startPosition < endPosition) {
                            downloadTextArea.positionCaret(endPosition);
                        }
                        downloadTextArea.appendText(it + StringUtils.LF);
                    });
                }
            });
        }
        return null;
    }
}
