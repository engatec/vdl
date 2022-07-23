package com.github.engatec.vdl.ui.component.core;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.github.engatec.vdl.core.ApplicationContext;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public abstract class AppComponent<T> {

    protected final ApplicationContext ctx = ApplicationContext.getInstance();

    protected Stage stage;

    public AppComponent(Stage stage) {
        this.stage = stage;
    }

    protected abstract String getFxmlPath();
    protected abstract T getController();

    public T load() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getFxmlPath()), ctx.getResourceBundle());

        T controller = getController();
        loader.setRoot(controller);
        loader.setController(controller);

        try {
            loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return controller;
    }
}
