package com.github.engatec.vdl.ui.controller.stage;

import com.github.engatec.vdl.core.AppExecutors;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgressDialogController extends StageAwareController {

    private static final Logger LOGGER = LogManager.getLogger(ProgressDialogController.class);

    private Task<?> task;
    private Runnable onSuccessCallback;

    @FXML private Button dialogProgressCancelButton;

    private ProgressDialogController() {
    }

    public ProgressDialogController(Stage stage, Task<?> task, Runnable onSuccessCallback) {
        super(stage);
        this.task = task;
        this.onSuccessCallback = onSuccessCallback;
    }

    @FXML
    public void initialize() {
        runTask();
    }

    private void runTask() {
        task.setOnSucceeded(event -> {
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            close(event);
        });

        task.setOnFailed(event -> {
            Throwable ex = event.getSource().getException();
            LOGGER.error(ex.getMessage(), ex);
            close(event);
        });

        task.setOnCancelled(this::close);

        dialogProgressCancelButton.setOnAction(e -> {
            task.cancel();
            e.consume();
        });

        AppExecutors.runTaskAsync(task);
    }

    private void close(Event e) {
        stage.close();
        e.consume();
    }
}
