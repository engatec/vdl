package com.github.engatec.vdl.controller.component;

import java.nio.file.Path;
import java.util.ResourceBundle;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.model.preferences.wrapper.misc.QueueAutostartDownloadPref;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

public class DownloadsComponentController extends VBox {

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

    @FXML private Button startDownloadBtn;
    @FXML private Button removeFinishedBtn;
    @FXML private Button removeAllBtn;
    @FXML private CheckBox autostartDownloadCheckbox;

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

        startDownloadBtn.setOnAction(this::handleStartDownloadButtonClick);
        removeFinishedBtn.setOnAction(this::handleRemoveFinishedButtonClick);
        removeAllBtn.setOnAction(this::handleRemoveAllButtonClick);
        autostartDownloadCheckbox.selectedProperty().bindBidirectional(ConfigRegistry.get(QueueAutostartDownloadPref.class).getProperty());

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
            queueManager.startDownload(row.getItem());
            e.consume();
        });

        MenuItem cancelMenuItem = new MenuItem(resourceBundle.getString("stage.queue.table.contextmenu.cancel"));
        cancelMenuItem.setOnAction(e -> {
            queueManager.cancelDownload(row.getItem());
            e.consume();
        });

        MenuItem resumeMenuItem = new MenuItem(resourceBundle.getString("stage.queue.table.contextmenu.resume"));
        resumeMenuItem.setOnAction(e -> {
            queueManager.resumeDownload(row.getItem());
            e.consume();
        });

        MenuItem deleteMenuItem = new MenuItem(resourceBundle.getString("stage.queue.table.contextmenu.delete"));
        deleteMenuItem.setOnAction(e -> {
            queueManager.removeItem(row.getItem());
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

            runNowMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() == DownloadStatus.READY, newValue.statusProperty()));
            cancelMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                            newValue.getStatus() == DownloadStatus.IN_PROGRESS || newValue.getStatus() == DownloadStatus.SCHEDULED,
                    newValue.statusProperty())
            );
            resumeMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() == DownloadStatus.CANCELLED, newValue.statusProperty()));
            deleteMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() != DownloadStatus.IN_PROGRESS, newValue.statusProperty()));
        });

        ctxMenu.getItems().addAll(runNowMenuItem, cancelMenuItem, resumeMenuItem, deleteMenuItem);
        return ctxMenu;
    }

    private void handleStartDownloadButtonClick(ActionEvent event) {
        for (QueueItem item : data) {
            if (item.getStatus() == DownloadStatus.READY) {
                queueManager.startDownload(item);
            }
        }
        event.consume();
    }

    private void handleRemoveFinishedButtonClick(ActionEvent event) {
        queueManager.removeFinished();
        event.consume();
    }

    private void handleRemoveAllButtonClick(ActionEvent event) {
        queueManager.removeAll();
        event.consume();
    }
}
