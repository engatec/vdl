package com.github.engatec.vdl.ui;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.FormattedResource;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.ui.stage.ProgressDialogStage;
import javafx.concurrent.Service;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Dialogs {

    public static void error(String key) {
        error(new FormattedResource(key));
    }

    public static void error(FormattedResource res) {
        var ctx = ApplicationContext.getInstance();
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ctx.getLocalizedString("error"));
        alert.setHeaderText(null);
        String msg = ctx.getLocalizedString(res.key());
        alert.setContentText(res.args() == null ? msg : String.format(msg, res.args()));
        alert.showAndWait();
    }

    public static void exception(String key, String msg) {
        var ctx = ApplicationContext.getInstance();
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ctx.getLocalizedString("error"));
        alert.setHeaderText(ctx.getLocalizedString(key));
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void info(String key) {
        info(new FormattedResource(key));
    }

    public static void info(FormattedResource res) {
        var ctx = ApplicationContext.getInstance();
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ctx.getLocalizedString("info"));
        alert.setHeaderText(null);
        String msg = ctx.getLocalizedString(res.key());
        alert.setContentText(res.args() == null ? msg : String.format(msg, res.args()));
        alert.showAndWait();
    }

    public static void info(String key, Language language) {
        var ctx = ApplicationContext.getInstance();
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ctx.getLocalizedString("info", language));
        alert.setHeaderText(null);
        alert.setContentText(ctx.getLocalizedString(key, language));
        alert.showAndWait();
    }

    public static void infoWithYesNoButtons(String key, Runnable onYesButtonClickHandler, Runnable onNoButtonClickHandler) {
        showDialogWithYesNoButtons(new FormattedResource(key), Alert.AlertType.INFORMATION, "info", onYesButtonClickHandler, onNoButtonClickHandler);
    }

    public static void infoWithYesNoButtons(FormattedResource res, Runnable onYesButtonClickHandler, Runnable onNoButtonClickHandler) {
        showDialogWithYesNoButtons(res, Alert.AlertType.INFORMATION, "info", onYesButtonClickHandler, onNoButtonClickHandler);
    }

    public static void warningWithYesNoButtons(String key, Runnable onYesButtonClickHandler, Runnable onNoButtonClickHandler) {
        showDialogWithYesNoButtons(new FormattedResource(key), Alert.AlertType.WARNING, "warning", onYesButtonClickHandler, onNoButtonClickHandler);
    }

    private static void showDialogWithYesNoButtons(FormattedResource res, Alert.AlertType alertType, String titleKey, Runnable onYesButtonClickHandler, Runnable onNoButtonClickHandler) {
        var ctx = ApplicationContext.getInstance();

        ButtonType yes = new ButtonType(ctx.getLocalizedString("button.yes"), ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType(ctx.getLocalizedString("button.no"), ButtonBar.ButtonData.NO);

        String msg = ctx.getLocalizedString(res.key());
        var alert = new Alert(alertType, res.args() == null ? msg : String.format(msg, res.args()), yes, no);
        alert.setTitle(ctx.getLocalizedString(titleKey));
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

    public static void progress(String key, Stage parent, Service<?> service) {
        String title = ApplicationContext.getInstance().getLocalizedString(key);
        new ProgressDialogStage(title, service).modal(parent).showAndWait();
    }
}
