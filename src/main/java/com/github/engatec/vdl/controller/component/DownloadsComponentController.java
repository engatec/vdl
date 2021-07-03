package com.github.engatec.vdl.controller.component;

import java.nio.file.Path;
import java.util.ResourceBundle;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.util.YouDlUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

public class DownloadsComponentController extends VBox implements ComponentController {

    private final QueueManager queueManager = QueueManager.INSTANCE;
    private final ObservableList<QueueItem> data = queueManager.getQueueItems();

    @FXML private TableView<QueueItem> downloadQueueTableView;
    @FXML private TableColumn<QueueItem, DownloadStatus> statusTableColumn;
    @FXML private TableColumn<QueueItem, Double> progressTableColumn;
    @FXML private TableColumn<QueueItem, String> titleTableColumn;
    @FXML private TableColumn<QueueItem, String> urlTableColumn;
    @FXML private TableColumn<QueueItem, String> sizeTableColumn;
    @FXML private TableColumn<QueueItem, String> throughputTableColumn;
    @FXML private TableColumn<QueueItem, Path> downloadPathTableColumn;

    @FXML private Button startAllBtn;
    @FXML private Button stopAllBtn;
    @FXML private Button removeAllBtn;

    @FXML
    public void initialize() {
        downloadQueueTableView.setPlaceholder(new Label(StringUtils.EMPTY));

        statusTableColumn.setCellValueFactory(it -> it.getValue().statusProperty());
        titleTableColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getTitle()));
        urlTableColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getBaseUrl()));
        sizeTableColumn.setCellValueFactory(it -> it.getValue().sizeProperty());
        throughputTableColumn.setCellValueFactory(it -> it.getValue().throughputProperty());
        downloadPathTableColumn.setCellValueFactory(it -> new ReadOnlyObjectWrapper<>(it.getValue().getDownloadPath()));

        progressTableColumn.setCellValueFactory(it -> it.getValue().progressProperty().asObject());
        progressTableColumn.setCellFactory(ProgressBarTableCell.forTableColumn());
        downloadQueueTableView.setItems(data);

        startAllBtn.setOnAction(this::handleStartAllButtonClick);
        stopAllBtn.setOnAction(this::handleStopAllButtonClick);
        removeAllBtn.setOnAction(this::handleRemoveAllButtonClick);

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
        ResourceBundle rb = ApplicationContext.INSTANCE.getResourceBundle();

        ContextMenu ctxMenu = new ContextMenu();

        MenuItem cancelMenuItem = new MenuItem(rb.getString("stage.queue.table.contextmenu.cancel"));
        cancelMenuItem.setOnAction(e -> {
            queueManager.cancelDownload(row.getItem());
            e.consume();
        });

        MenuItem resumeMenuItem = new MenuItem(rb.getString("stage.queue.table.contextmenu.resume"));
        resumeMenuItem.setOnAction(e -> {
            queueManager.resumeDownload(row.getItem());
            e.consume();
        });

        MenuItem deleteMenuItem = new MenuItem(rb.getString("stage.queue.table.contextmenu.delete"));
        deleteMenuItem.setOnAction(e -> {
            QueueItem item = row.getItem();
            YouDlUtils.deleteTempFiles(item.getDestinations());
            queueManager.removeItem(item);
            e.consume();
        });

        row.itemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                cancelMenuItem.visibleProperty().unbind();
                resumeMenuItem.visibleProperty().unbind();
                deleteMenuItem.visibleProperty().unbind();
                return;
            }

            cancelMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                            newValue.getStatus() == DownloadStatus.IN_PROGRESS || newValue.getStatus() == DownloadStatus.SCHEDULED,
                    newValue.statusProperty())
            );
            resumeMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() == DownloadStatus.CANCELLED, newValue.statusProperty()));
            deleteMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() != DownloadStatus.IN_PROGRESS, newValue.statusProperty()));
        });

        ctxMenu.getItems().addAll(cancelMenuItem, resumeMenuItem, deleteMenuItem);
        return ctxMenu;
    }

    private void handleStartAllButtonClick(ActionEvent event) {
        for (QueueItem item : data) {
            DownloadStatus status = item.getStatus();
            if (status == DownloadStatus.READY) {
                queueManager.startDownload(item);
            } else if (status == DownloadStatus.CANCELLED) {
                queueManager.resumeDownload(item);
            }
        }
        event.consume();
    }

    private void handleStopAllButtonClick(ActionEvent event) {
        for (QueueItem item : data) {
            DownloadStatus status = item.getStatus();
            if (status == DownloadStatus.SCHEDULED || status == DownloadStatus.IN_PROGRESS) {
                queueManager.cancelDownload(item);
            }
        }
        event.consume();
    }

    private void handleRemoveAllButtonClick(ActionEvent event) {
        queueManager.removeAll();
        event.consume();
    }
}
