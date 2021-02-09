package com.github.engatec.vdl.ui;

import com.github.engatec.vdl.core.ApplicationContext;
import javafx.scene.control.Alert;

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
}
