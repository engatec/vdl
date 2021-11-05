package com.github.engatec.vdl.worker;

import java.io.IOException;

import com.github.engatec.vdl.core.YoutubeDlManager;
import javafx.concurrent.Task;

public class UpdateYoutubeDlBinaryTask extends Task<Void> {

    @Override
    protected Void call() throws IOException, InterruptedException {
        YoutubeDlManager.INSTANCE.updateYoutubeDl();
        return null;
    }
}
