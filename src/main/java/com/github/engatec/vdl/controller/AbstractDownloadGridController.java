package com.github.engatec.vdl.controller;

import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.action.DownloadAction;
import com.github.engatec.vdl.model.Downloadable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public abstract class AbstractDownloadGridController extends GridPane {

    protected Button createDownloadButton(Stage parentStage, Downloadable downloadable) {
        Button downloadBtn = new Button();
        I18n.bindLocaleProperty(downloadBtn.textProperty(), "download");
        downloadBtn.setOnAction(e -> {
            new DownloadAction(parentStage, downloadable).perform();
            e.consume();
        });
        return downloadBtn;
    }
}
