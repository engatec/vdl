package com.github.engatec.vdl.stage;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.github.engatec.vdl.core.ApplicationContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

public abstract class AppStage {

    protected Stage stage = new Stage();

    protected abstract String getFxmlPath();
    protected abstract Callback<Class<?>, Object> getControllerFactory(Stage stage);

    protected void load() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getFxmlPath()), ApplicationContext.INSTANCE.getResourceBundle());
        loader.setControllerFactory(getControllerFactory(stage));

        Parent rootNode;
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        stage.setScene(new Scene(rootNode));
    }

    public AppStage modal(Window owner) {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        return this;
    }

    public void show() {
        stage.show();
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}
