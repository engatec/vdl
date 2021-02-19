package com.github.engatec.vdl.controller;

import java.nio.file.Path;

import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.action.DownloadQueueItemAction;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class QueueController extends StageAwareController {

    private final ObservableList<QueueItem> data = QueueManager.INSTANCE.getQueueItems();

    @FXML private TableView<QueueItem> downloadQueueTableView;
    @FXML private TableColumn<QueueItem, DownloadStatus> statusTableColumn;
    @FXML private TableColumn<QueueItem, Double> progressTableColumn;
    @FXML private TableColumn<QueueItem, String> urlTableColumn;
    @FXML private TableColumn<QueueItem, String> sizeTableColumn;
    @FXML private TableColumn<QueueItem, String> throughputTableColumn;
    @FXML private TableColumn<QueueItem, Path> downloadPathTableColumn;

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
        urlTableColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        sizeTableColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        throughputTableColumn.setCellValueFactory(new PropertyValueFactory<>("throughput"));
        downloadPathTableColumn.setCellValueFactory(new PropertyValueFactory<>("downloadPath"));

        progressTableColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        progressTableColumn.setCellFactory(ProgressBarTableCell.forTableColumn());
        downloadQueueTableView.setItems(data);

        startDownloadBtn.setOnAction(this::handleStartDownloadButtonClick);
        cleanupBtn.setOnAction(this::handleCleanupButtonClick);
        closeBtn.setOnAction(this::handleCloseButtonClick);
    }

    private void handleStartDownloadButtonClick(ActionEvent event) {
        for (QueueItem item : data) {
            if (item.getStatus() == DownloadStatus.READY) {
                new DownloadQueueItemAction(item).perform();
            }
        }
    }

    private void handleCleanupButtonClick(ActionEvent event) {
        data.removeIf(item -> item.getStatus() == DownloadStatus.FINISHED);
    }

    private void handleCloseButtonClick(ActionEvent event) {
        stage.close();
        event.consume();
    }
}
