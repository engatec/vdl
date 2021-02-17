package com.github.engatec.vdl.ui;

import com.github.engatec.vdl.controller.dialog.ProgressDialogController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.UiComponent;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
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
        Stage stage = Stages.newModalStage(UiComponent.DIALOG_PROGRESS, s -> new ProgressDialogController(s, title, task), parent);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
