package com.github.engatec.vdl.ui.component.controller;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.FormattedResource;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.preference.model.TableConfigModel;
import com.github.engatec.vdl.preference.property.table.DownloadsTableConfigProperty;
import com.github.engatec.vdl.ui.helper.Dialogs;
import com.github.engatec.vdl.ui.helper.Tables;
import com.github.engatec.vdl.ui.scene.control.cell.ProgressBarWithPercentTableCell;
import com.github.engatec.vdl.util.AppUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class DownloadsComponentController extends VBox implements ComponentController {

    private final Stage stage;

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

    public DownloadsComponentController(Stage stage) {
        this.stage = stage;
    }

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

        var selectionModel = downloadQueueTableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        ContextMenu multipleRowsContextMenu = createMultipleRowsContextMenu(selectionModel);
        BooleanBinding multipleRowsSelected = Bindings.createBooleanBinding(() -> selectionModel.getSelectedItems().size() > 1, selectionModel.getSelectedItems());
        downloadQueueTableView.contextMenuProperty().bind(
                Bindings.when(multipleRowsSelected)
                        .then(multipleRowsContextMenu)
                        .otherwise((ContextMenu) null)
        );

        downloadQueueTableView.setRowFactory(tableView -> {
            TableRow<QueueItem> row = new TableRow<>();

            ContextMenu singleRowContextMenu = createSingleRowContextMenu(row, selectionModel);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty().not().and(multipleRowsSelected.not()))
                            .then(singleRowContextMenu)
                            .otherwise((ContextMenu) null)
            );

            row.setOnDragDetected(event -> {
                startFullDrag();
                event.consume();
            });

            row.setOnMouseDragEntered(event -> {
                selectionModel.select(row.getIndex());
                event.consume();
            });

            return row;
        });

        TableConfigModel tableConfigModel = ctx.getConfigRegistry().get(DownloadsTableConfigProperty.class).getValue();
        Tables.restoreTableViewStateFromConfigModel(downloadQueueTableView, tableConfigModel, COLUMN_ID_MAP);
    }

    private ContextMenu createMultipleRowsContextMenu(TableView.TableViewSelectionModel<QueueItem> selectionModel) {
        var ctxMenu = new ContextMenu();

        var startSelectedMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.startselected"));
        startSelectedMenuItem.setOnAction(e -> {
            startItems(selectionModel.getSelectedItems());
            e.consume();
        });

        var stopSelectedMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.stopselected"));
        stopSelectedMenuItem.setOnAction(e -> {
            stopItems(selectionModel.getSelectedItems());
            e.consume();
        });

        var removeSelectedMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.removeselected"));
        removeSelectedMenuItem.setOnAction(e -> {
            List<QueueItem> itemsForRemoval = List.copyOf(selectionModel.getSelectedItems()); // Copy of the list is required as removal changes selection and leads to IndexOutOfBoundsException
            for (QueueItem item : itemsForRemoval) {
                queueManager.removeItem(item);
            }
            selectionModel.clearSelection();
            e.consume();
        });

        var changePathMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.changepath"));
        changePathMenuItem.setOnAction(e -> {
            Dialogs.infoWithYesNoButtons(
                    new FormattedResource("stage.queue.multiplepathchange.info", DownloadStatus.STOPPED.toString()),
                    () -> AppUtils.choosePath(stage).ifPresent(newPath -> queueManager.changeDownloadPath(selectionModel.getSelectedItems(), newPath)),
                    null
            );
            e.consume();
        });

        var copyUrlsMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.copyurls"));
        copyUrlsMenuItem.setOnAction(e -> {
            String urls = selectionModel.getSelectedItems().stream()
                    .map(QueueItem::getBaseUrl)
                    .collect(Collectors.joining(System.lineSeparator()));

            ClipboardContent content = new ClipboardContent();
            content.putString(urls);
            Clipboard.getSystemClipboard().setContent(content);

            e.consume();
        });

        ctxMenu.getItems().addAll(startSelectedMenuItem, stopSelectedMenuItem, removeSelectedMenuItem, changePathMenuItem, copyUrlsMenuItem);

        return ctxMenu;
    }

    private ContextMenu createSingleRowContextMenu(TableRow<QueueItem> row, TableView.TableViewSelectionModel<QueueItem> selectionModel) {
        ContextMenu ctxMenu = new ContextMenu();

        MenuItem cancelMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.stop"));
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
            selectionModel.clearSelection();
            e.consume();
        });

        MenuItem changePathMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.changepath"));
        changePathMenuItem.setOnAction(e -> {
            AppUtils.choosePath(stage).ifPresent(newPath -> queueManager.changeDownloadPath(List.of(row.getItem()), newPath));
            e.consume();
        });

        MenuItem copyUrlMenuItem = new MenuItem(ctx.getLocalizedString("stage.queue.table.contextmenu.copyurl"));
        copyUrlMenuItem.setOnAction(e -> {
            var content = new ClipboardContent();
            content.putString(row.getItem().getBaseUrl());
            Clipboard.getSystemClipboard().setContent(content);
            e.consume();
        });

        row.itemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                cancelMenuItem.visibleProperty().unbind();
                resumeMenuItem.visibleProperty().unbind();
                retryMenuItem.visibleProperty().unbind();
                deleteMenuItem.visibleProperty().unbind();
                changePathMenuItem.visibleProperty().unbind();
                return;
            }

            cancelMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() ->
                            newValue.getStatus() == DownloadStatus.IN_PROGRESS || newValue.getStatus() == DownloadStatus.SCHEDULED,
                    newValue.statusProperty())
            );
            resumeMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() == DownloadStatus.STOPPED, newValue.statusProperty()));
            retryMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() == DownloadStatus.FAILED, newValue.statusProperty()));
            deleteMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() != DownloadStatus.IN_PROGRESS, newValue.statusProperty()));
            changePathMenuItem.visibleProperty().bind(Bindings.createBooleanBinding(() -> newValue.getStatus() == DownloadStatus.STOPPED, newValue.statusProperty()));
        });

        ctxMenu.getItems().addAll(cancelMenuItem, resumeMenuItem, retryMenuItem, deleteMenuItem, changePathMenuItem, copyUrlMenuItem);
        return ctxMenu;
    }

    private void handleStartAllButtonClick(ActionEvent event) {
        startItems(data);
        event.consume();
    }

    private void startItems(List<QueueItem> items) {
        for (QueueItem item : items) {
            DownloadStatus status = item.getStatus();
            if (status == DownloadStatus.READY) {
                queueManager.startDownload(item);
            } else if (status == DownloadStatus.STOPPED) {
                queueManager.resumeDownload(item);
            }
        }
    }

    private void handleStopAllButtonClick(ActionEvent event) {
        stopItems(data);
        event.consume();
    }

    private void stopItems(List<QueueItem> items) {
        for (QueueItem item : items) {
            DownloadStatus status = item.getStatus();
            if (status == DownloadStatus.SCHEDULED || status == DownloadStatus.IN_PROGRESS) {
                queueManager.cancelDownload(item);
            }
        }
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
