package com.github.engatec.vdl.ui;

import java.util.Optional;
import java.util.ResourceBundle;

import com.github.engatec.vdl.controller.dialog.ProgressDialogController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.UiComponent;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Dialogs {

    public static void error(String key) {
        ResourceBundle resBundle = ApplicationContext.INSTANCE.getResourceBundle();
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(resBundle.getString("error"));
        alert.setHeaderText(null);
        alert.setContentText(resBundle.getString(key));
        alert.showAndWait();
    }

    public static void info(String key) {
        ResourceBundle resBundle = ApplicationContext.INSTANCE.getResourceBundle();
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(resBundle.getString("info"));
        alert.setHeaderText(null);
        alert.setContentText(resBundle.getString(key));
        alert.showAndWait();
    }

    public static Optional<ButtonType> warningWithYesNoButtons(String key) {
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();
        ButtonType yes = new ButtonType(resourceBundle.getString("button.yes"), ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType(resourceBundle.getString("button.no"), ButtonBar.ButtonData.NO);
        var alert = new Alert(Alert.AlertType.WARNING, resourceBundle.getString(key), yes, no);
        alert.setTitle(resourceBundle.getString("warning"));
        alert.setHeaderText(null);
        return alert.showAndWait();
    }

    public static void progress(String title, Stage parent, Task<?> task) {
        Stage stage = Stages.newModalStage(UiComponent.DIALOG_PROGRESS, s -> new ProgressDialogController(s, title, task), parent);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
