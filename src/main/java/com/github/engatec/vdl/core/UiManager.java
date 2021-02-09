package com.github.engatec.vdl.core;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.github.engatec.vdl.controller.StageAware;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UiManager {

    private static FXMLLoader createFXMLLoader(UiComponent uiComponent) {
        return new FXMLLoader(UiManager.class.getResource(uiComponent.getFxml()), ApplicationContext.INSTANCE.getResourceBundle());
    }

    public static Stage loadStage(UiComponent uiComponent) {
        return loadStage(uiComponent, new Stage());
    }

    public static Stage loadStage(UiComponent uiComponent, Stage stage) {
        return loadStage(uiComponent, stage, null);
    }

    public static Stage loadStage(UiComponent uiComponent, Stage stage, Callback<Class<?>, Object> controllerFactory) {
        FXMLLoader loader = createFXMLLoader(uiComponent);
        if (controllerFactory != null) {
            loader.setControllerFactory(controllerFactory);
        }

        Parent parent;
        try {
            parent = loader.load();
            Object controller = loader.getController();
            if (controller instanceof StageAware) {
                ((StageAware) controller).setStage(stage);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        stage.setScene(new Scene(parent));

        return stage;
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
