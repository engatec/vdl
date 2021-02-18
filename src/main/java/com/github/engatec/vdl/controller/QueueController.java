package com.github.engatec.vdl.controller;

import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class QueueController extends StageAwareController {

    @FXML private TableView<QueueItem> downloadQueueTableView;
    @FXML private TableColumn<QueueItem, DownloadStatus> statusTableColumn;
    @FXML private TableColumn<QueueItem, String> urlTableColumn;

    @FXML private Button startDownloadBtn;
    @FXML private Button cleanupBtn;
    @FXML private Button closeBtn;

    private QueueController() {
    }

    public QueueController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        statusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        urlTableColumn.setCellValueFactory(new PropertyValueFactory<>("p"));
        downloadQueueTableView.setItems(FXCollections.observableList(QueueManager.INSTANCE.getQueueItems()));

        startDownloadBtn.setOnAction(this::handleStartDownloadButtonClick);
        closeBtn.setOnAction(this::handleCloseButtonClick);
    }

    private void handleStartDownloadButtonClick(ActionEvent event) {
        /*data.get(0).setStatus(DownloadStatus.FAILED);
        data.get(0).setUrl("CHANGED!!!");*/
    }

    private void handleCloseButtonClick(ActionEvent event) {
        stage.close();
        event.consume();
    }
}
