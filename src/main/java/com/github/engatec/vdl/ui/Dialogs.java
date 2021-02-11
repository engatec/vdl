package com.github.engatec.vdl.ui;

import com.github.engatec.vdl.controller.dialog.ProgressDialogController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Dialogs {

    public static void error(String message) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ApplicationContext.INSTANCE.getResourceBundle().getString("error"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void info(String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ApplicationContext.INSTANCE.getResourceBundle().getString("info"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void progress(String title, Stage parent, Task<?> task) {
        Stage stage = UiManager.loadStage(UiComponent.DIALOG_PROGRESS, new Stage(), param -> new ProgressDialogController(title, task));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parent);
        stage.showAndWait();
    }
}
