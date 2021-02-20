package com.github.engatec.vdl.controller;

import java.nio.file.Path;
import java.util.ResourceBundle;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.action.DownloadQueueItemAction;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.commons.lang3.NotImplementedException;

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
        downloadQueueTableView.setPlaceholder(new Label(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.queue.table.placeholder")));

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

        downloadQueueTableView.setRowFactory(tableView -> {
            TableRow<QueueItem> row = new TableRow<>();
            ContextMenu contextMenu = createContextMenu(row);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty().not())
                            .then(contextMenu)
                            .otherwise((ContextMenu) null)
            );
            return row;
        });
    }

    private ContextMenu createContextMenu(TableRow<QueueItem> row) {
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();
        ContextMenu ctxMenu = new ContextMenu();

        MenuItem runNowMenuItem = new MenuItem(resourceBundle.getString("stage.queue.table.contextmenu.runnow"));
        runNowMenuItem.setOnAction(e -> {
            new DownloadQueueItemAction(row.getItem()).perform();
            e.consume();
        });

        MenuItem cancelMenuItem = new MenuItem(resourceBundle.getString("stage.queue.table.contextmenu.cancel"));
        cancelMenuItem.setOnAction(e -> {
            throw new NotImplementedException();
            // e.consume();
        });

        MenuItem resumeMenuItem = new MenuItem(resourceBundle.getString("stage.queue.table.contextmenu.resume"));
        resumeMenuItem.setOnAction(e -> {
            new DownloadQueueItemAction(row.getItem()).perform();
            e.consume();
        });

        MenuItem deleteMenuItem = new MenuItem(resourceBundle.getString("stage.queue.table.contextmenu.delete"));
        deleteMenuItem.setOnAction(e -> {
            data.remove(row.getItem());
            e.consume();
        });

        row.itemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                runNowMenuItem.visibleProperty().unbind();
                cancelMenuItem.visibleProperty().unbind();
                resumeMenuItem.visibleProperty().unbind();
                deleteMenuItem.visibleProperty().unbind();
                return;
            }

            DownloadStatus currentStatus = newValue.getStatus();
            ObjectProperty<DownloadStatus> itemStatusProperty = newValue.statusProperty();
            runNowMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> currentStatus == DownloadStatus.READY, itemStatusProperty));
            cancelMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> currentStatus == DownloadStatus.IN_PROGRESS, itemStatusProperty));
            resumeMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> currentStatus == DownloadStatus.CANCELLED, itemStatusProperty));
            deleteMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> currentStatus != DownloadStatus.IN_PROGRESS, itemStatusProperty));
        });

        ctxMenu.getItems().addAll(runNowMenuItem, deleteMenuItem);
        return ctxMenu;
    }

    private void handleStartDownloadButtonClick(ActionEvent event) {
        for (QueueItem item : data) {
            if (item.getStatus() == DownloadStatus.READY) {
                new DownloadQueueItemAction(item).perform();
            }
        }
        event.consume();
    }

    private void handleCleanupButtonClick(ActionEvent event) {
        data.removeIf(item -> item.getStatus() == DownloadStatus.FINISHED);
        event.consume();
    }

    private void handleCloseButtonClick(ActionEvent event) {
        stage.close();
        event.consume();
    }
}
