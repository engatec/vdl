package com.github.engatec.vdl.ui.controller.stage;

import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ProgressDialogController extends StageAwareController {

    private Service<?> service;

    @FXML private Button dialogProgressCancelButton;

    private ProgressDialogController() {
    }

    public ProgressDialogController(Stage stage, Service<?> service) {
        super(stage);
        this.service = service;
    }

    @FXML
    public void initialize() {
        EventHandler<WorkerStateEvent> onSucceeded = service.getOnSucceeded();
        service.setOnSucceeded(event -> {
            if (onSucceeded != null) {
                onSucceeded.handle(event);
            }
            close(event);
        });

        EventHandler<WorkerStateEvent> onFailed = service.getOnFailed();
        service.setOnFailed(event -> {
            if (onFailed != null) {
                onFailed.handle(event);
            }
            close(event);
        });

        EventHandler<WorkerStateEvent> onCancelled = service.getOnCancelled();
        service.setOnCancelled(event -> {
            if (onCancelled != null) {
                onCancelled.handle(event);
            }
            close(event);
        });

        dialogProgressCancelButton.setOnAction(e -> service.cancel());

        service.start();
    }

    private void close(Event e) {
        stage.close();
        e.consume();
    }
}
