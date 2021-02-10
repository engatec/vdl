package com.github.engatec.vdl.worker;

import java.io.IOException;

import com.github.engatec.vdl.core.DownloadManager;
import javafx.concurrent.Task;

public class UpdateBinariesTask extends Task<Void> {

    @Override
    protected Void call() throws IOException, InterruptedException {
        DownloadManager.INSTANCE.checkYoutubeDlUpdates();
        return null;
    }
}
