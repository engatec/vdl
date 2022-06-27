package com.github.engatec.vdl.ui.component.controller;

import java.nio.file.Path;
import java.util.Map;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.preference.model.TableConfigModel;
import com.github.engatec.vdl.preference.property.table.DownloadsTableConfigProperty;
import com.github.engatec.vdl.ui.helper.Tables;
import com.github.engatec.vdl.ui.scene.control.cell.ProgressBarWithPercentTableCell;
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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

public class DownloadsComponentController extends VBox implements ComponentController {

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final QueueManager queueManager = ctx.getManager(QueueManager.class);
    private final ObservableList<QueueItem> data = queueManager.getQueueItems();

    // This map allows to change column field names without affecting the code as ids are used to save and restore table view state
    private static final Map<String, Integer> COLUMN_ID_MAP = Map.of(
            "statusTableColumn", 1,
            "progressTableColumn", 2,
            "titleTableColumn", 3,
            "urlTableColumn", 4,
            "sizeTableColumn", 5,
            "throughputTableColumn", 6,
            "downloadPathTableColumn", 7
    );

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
        progressTableColumn.setCellFactory(ProgressBarWithPercentTableCell.forTableColumn());
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

        TableConfigModel tableConfigModel = ctx.getConfigRegistry().get(DownloadsTableConfigProperty.class).getValue();
        Tables.restoreTableViewStateFromConfigModel(downloadQueueTableView, tableConfigModel, COLUMN_ID_MAP);
    }

    private ContextMenu createContextMenu(TableRow<QueueItem> row) {
        ContextMenu ctxMenu = new ContextMenu();

        MenuItem copyUrlMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.copyurl"));
        copyUrlMenuItem.setOnAction(e -> {
            var content = new ClipboardContent();
            content.putString(row.getItem().getBaseUrl());
            Clipboard.getSystemClipboard().setContent(content);
            e.consume();
        });

        MenuItem cancelMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.cancel"));
        cancelMenuItem.setOnAction(e -> {
            queueManager.cancelDownload(row.getItem());
            e.consume();
        });

        MenuItem resumeMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.resume"));
        resumeMenuItem.setOnAction(e -> {
            queueManager.resumeDownload(row.getItem());
            e.consume();
        });

        MenuItem retryMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.retry"));
        retryMenuItem.setOnAction(e -> {
            queueManager.retryDownload(row.getItem());
            e.consume();
        });

        MenuItem deleteMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.delete"));
        deleteMenuItem.setOnAction(e -> {
            queueManager.removeItem(row.getItem());
            e.consume();
        });

        row.itemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                cancelMenuItem.visibleProperty().unbind();
                resumeMenuItem.visibleProperty().unbind();
                retryMenuItem.visibleProperty().unbind();
                deleteMenuItem.visibleProperty().unbind();
                return;
            }

            cancelMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                            newValue.getStatus() == DownloadStatus.IN_PROGRESS || newValue.getStatus() == DownloadStatus.SCHEDULED,
                    newValue.statusProperty())
            );
            resumeMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() == DownloadStatus.STOPPED, newValue.statusProperty()));
            retryMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() == DownloadStatus.FAILED, newValue.statusProperty()));
            deleteMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() != DownloadStatus.IN_PROGRESS, newValue.statusProperty()));
        });

        ctxMenu.getItems().addAll(cancelMenuItem, resumeMenuItem, retryMenuItem, deleteMenuItem, copyUrlMenuItem);
        return ctxMenu;
    }

    private void handleStartAllButtonClick(ActionEvent event) {
        for (QueueItem item : data) {
            DownloadStatus status = item.getStatus();
            if (status == DownloadStatus.READY) {
                queueManager.startDownload(item);
            } else if (status == DownloadStatus.STOPPED) {
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

    @Override
    public void onVisibilityLost() {
        var prop = ctx.getConfigRegistry().get(DownloadsTableConfigProperty.class);
        prop.setValue(Tables.convertTableViewStateToConfigModel(downloadQueueTableView, COLUMN_ID_MAP));
        prop.save();
    }
}
