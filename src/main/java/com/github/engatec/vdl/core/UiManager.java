package com.github.engatec.vdl.core;

import java.io.IOException;
import java.io.UncheckedIOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UiManager {

    private static FXMLLoader createFXMLLoader(UiComponent uiComponent) {
        return new FXMLLoader(UiManager.class.getResource(uiComponent.getFxml()), ApplicationContext.INSTANCE.getResourceBundle());
    }

    public static void loadStage(UiComponent uiComponent, Stage stage, Callback<Class<?>, Object> controllerFactory) {
        FXMLLoader loader = createFXMLLoader(uiComponent);
        if (controllerFactory != null) {
            loader.setControllerFactory(controllerFactory);
        }

        Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        stage.setScene(new Scene(parent));
    }

    public static Parent loadComponent(UiComponent uiComponent, Callback<Class<?>, Object> controllerFactory) {
        FXMLLoader loader = createFXMLLoader(uiComponent);
        if (controllerFactory != null) {
            loader.setControllerFactory(controllerFactory);
        }
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
