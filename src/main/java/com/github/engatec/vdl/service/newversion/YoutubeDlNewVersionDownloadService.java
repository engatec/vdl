package com.github.engatec.vdl.service.newversion;

import java.io.IOException;

import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.core.YoutubeDlManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class YoutubeDlNewVersionDownloadService extends Service<Void> {

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() throws IOException, InterruptedException {
                YoutubeDlManager.INSTANCE.updateVersion(Engine.YOUTUBE_DL);
                return null;
            }
        };
    }
}
