package com.github.engatec.vdl.ui;

import java.util.ResourceBundle;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.ui.stage.ProgressDialogStage;
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

    public static void infoWithYesNoButtons(String key, Runnable onYesButtonClickHandler, Runnable onNoButtonClickHandler) {
        showDialogWithYesNoButtons(key, Alert.AlertType.INFORMATION, "info", onYesButtonClickHandler, onNoButtonClickHandler);
    }

    public static void warningWithYesNoButtons(String key, Runnable onYesButtonClickHandler, Runnable onNoButtonClickHandler) {
        showDialogWithYesNoButtons(key, Alert.AlertType.WARNING, "warning", onYesButtonClickHandler, onNoButtonClickHandler);
    }

    private static void showDialogWithYesNoButtons(String messageKey, Alert.AlertType alertType, String titleKey, Runnable onYesButtonClickHandler, Runnable onNoButtonClickHandler) {
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        ButtonType yes = new ButtonType(resourceBundle.getString("button.yes"), ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType(resourceBundle.getString("button.no"), ButtonBar.ButtonData.NO);

        var alert = new Alert(alertType, resourceBundle.getString(messageKey), yes, no);
        alert.setTitle(resourceBundle.getString(titleKey));
        alert.setHeaderText(null);

        ButtonBar.ButtonData result = alert.showAndWait()
                .map(ButtonType::getButtonData)
                .orElse(ButtonBar.ButtonData.NO);

        if (result == ButtonBar.ButtonData.YES) {
            if (onYesButtonClickHandler != null) {
                onYesButtonClickHandler.run();
            }
        } else if (onNoButtonClickHandler != null) {
            onNoButtonClickHandler.run();
        }
    }

    public static void progress(String title, Stage parent, Task<?> task, Runnable onSuccessCallback) {
        new ProgressDialogStage(title, task, onSuccessCallback).modal(parent).showAndWait();
    }
}
