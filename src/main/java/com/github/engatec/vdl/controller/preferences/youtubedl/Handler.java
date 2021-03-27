package com.github.engatec.vdl.controller.preferences.youtubedl;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

class Handler {

    static void handleConfigFileChooseBtnClick(Context ctx, ActionEvent event) {
        var fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(ctx.getStage());
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            ctx.getConfigFileTextField().setText(path);
        }
        event.consume();
    }
}
