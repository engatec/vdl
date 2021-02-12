package com.github.engatec.vdl.controller.dialog;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.core.ApplicationContext;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgressDialogController extends StageAwareController {

    private static final Logger LOGGER = LogManager.getLogger(ProgressDialogController.class);

    private String title;
    private Task<?> task;

    @FXML private Label progressDialogTitleLabel;
    @FXML private Button dialogProgressCancelButton;

    private ProgressDialogController() {
    }

    public ProgressDialogController(Stage stage, String title, Task<?> task) {
        super(stage);
        this.title = title;
        this.task = task;
    }

    @FXML
    public void initialize() {
        progressDialogTitleLabel.setText(title);
        runTask();
    }

    private void runTask() {
        task.setOnSucceeded(this::close);
        task.setOnFailed(it -> {
            Throwable ex = it.getSource().getException();
            LOGGER.error(ex.getMessage(), ex);
        });
        task.setOnCancelled(this::close);
        dialogProgressCancelButton.setOnAction(e -> {
            task.cancel();
            e.consume();
        });
        ApplicationContext.INSTANCE.runTaskAsync(task);
    }

    private void close(Event e) {
        stage.close();
        e.consume();
    }
}
