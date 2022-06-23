package com.github.engatec.vdl.ui.component.controller.history;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.HistoryManager;
import com.github.engatec.vdl.handler.ComboBoxRollingScrollHandler;
import com.github.engatec.vdl.model.HistoryItem;
import com.github.engatec.vdl.preference.model.TableColumnConfigModel;
import com.github.engatec.vdl.preference.model.TableConfigModel;
import com.github.engatec.vdl.preference.property.misc.HistoryEntriesNumberConfigProperty;
import com.github.engatec.vdl.preference.property.misc.HistoryTableConfigProperty;
import com.github.engatec.vdl.ui.component.controller.ComponentController;
import com.github.engatec.vdl.ui.helper.Dialogs;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HistoryComponentController extends VBox implements ComponentController {

    private static final Logger LOGGER = LogManager.getLogger(HistoryComponentController.class);

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final HistoryManager historyManager = ctx.getManager(HistoryManager.class);

    // This map allows to change column field names without affecting the code as ids are used to save and restore table view state
    private static final Map<String, Integer> COLUMN_ID_MAP = new HashMap<>();
    static {
        COLUMN_ID_MAP.put("titleTableColumn", 1);
        COLUMN_ID_MAP.put("urlTableColumn", 2);
        COLUMN_ID_MAP.put("locationTableColumn", 3);
        COLUMN_ID_MAP.put("dtmTableColumn", 4);
    }

    @FXML private TableView<HistoryItem> historyTableView;
    @FXML private TableColumn<HistoryItem, String> titleTableColumn;
    @FXML private TableColumn<HistoryItem, String> urlTableColumn;
    @FXML private TableColumn<HistoryItem, Path> locationTableColumn;
    @FXML private TableColumn<HistoryItem, String> dtmTableColumn;

    @FXML private ComboBox<Integer> entriesNumberComboBox;
    @FXML private Button clearHistoryBtn;

    @FXML
    public void initialize() {
        initEntriesNumberComboBox();
        initHistoryTableView();

        clearHistoryBtn.setOnAction(this::handleClearHistoryBtnClick);
    }

    private void initEntriesNumberComboBox() {
        final String DISABLE_HISTORY = ctx.getLocalizedString("stage.history.combobox.disablehistory");

        entriesNumberComboBox.setItems(FXCollections.observableArrayList(0, 10, 30, 50, 100, 1000));
        entriesNumberComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                if (object == null) {
                    return null;
                }

                return object == 0 ? DISABLE_HISTORY : String.valueOf(object);
            }

            @Override
            public Integer fromString(String string) {
                return DISABLE_HISTORY.equals(string) ? 0 : Integer.parseInt(string);
            }
        });

        entriesNumberComboBox.valueProperty().bindBidirectional(ctx.getConfigRegistry().get(HistoryEntriesNumberConfigProperty.class).getProperty());
        entriesNumberComboBox.setOnScroll(new ComboBoxRollingScrollHandler());
    }

    private void initHistoryTableView() {
        historyTableView.setPlaceholder(new Label(ctx.getLocalizedString("stage.history.table.placeholder")));

        titleTableColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getTitle()));
        urlTableColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getUrl()));
        locationTableColumn.setCellValueFactory(it -> new ReadOnlyObjectWrapper<>(it.getValue().getDownloadPath()));
        dtmTableColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getCreatedAt()));

        historyTableView.setRowFactory(tableView -> {
            TableRow<HistoryItem> row = new TableRow<>();
            ContextMenu contextMenu = createContextMenu(row);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty().not())
                            .then(contextMenu)
                            .otherwise((ContextMenu) null)
            );
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    playFile(row.getItem().getDownloadPath());
                }
            });

            return row;
        });

        historyTableView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && event.getSource() instanceof TableView<?> tv && tv.getSelectionModel().getSelectedItem() instanceof HistoryItem item) {
                playFile(item.getDownloadPath());
            }
        });
    }

    private void restoreTableViewStateFromConfig() {
        TableConfigModel tableConfigModel = ctx.getConfigRegistry().get(HistoryTableConfigProperty.class).getValue();

        Map<Integer, TableColumnConfigModel> tableConfigModelMap = SetUtils.emptyIfNull(tableConfigModel.columnConfigModels()).stream()
                .collect(Collectors.toMap(TableColumnConfigModel::id, Function.identity()));

        if (tableConfigModelMap.isEmpty()) {
            return;
        }

        historyTableView.getColumns().sort((c1, c2) -> {
            TableColumnConfigModel c1Config = tableConfigModelMap.get(COLUMN_ID_MAP.get(c1.getId()));
            TableColumnConfigModel c2Config = tableConfigModelMap.get(COLUMN_ID_MAP.get(c2.getId()));
            return Integer.compare(c1Config.pos(), c2Config.pos());
        });

        historyTableView.getColumns().forEach(column -> {
            TableColumnConfigModel config = tableConfigModelMap.get(COLUMN_ID_MAP.get(column.getId()));
            if (config == null) {
                LOGGER.warn("Config for column {} is null", column.getId());
                return;
            }

            column.setPrefWidth(config.width());
            column.setSortType(config.sortType());
        });

        // Restore sort order
        List<Integer> sortOrderColumnIds = ListUtils.emptyIfNull(tableConfigModel.sortOrderColumnIds());
        List<TableColumn<HistoryItem, ?>> sortOrderColumns = new ArrayList<>(sortOrderColumnIds.size());
        for (int i = 0; i < sortOrderColumnIds.size(); i++) {
            sortOrderColumns.add(null);
        }

        historyTableView.getColumns().forEach(column -> {
            int sortOrderIdx = sortOrderColumnIds.indexOf(COLUMN_ID_MAP.get(column.getId()));
            if (sortOrderIdx >= 0) {
                sortOrderColumns.set(sortOrderIdx, column);
            }
        });

        historyTableView.getSortOrder().addAll(sortOrderColumns);
    }

    private ContextMenu createContextMenu(TableRow<HistoryItem> row) {
        ContextMenu ctxMenu = new ContextMenu();

        MenuItem play = new MenuItem(ctx.getLocalizedString("stage.history.ctxmenu.play"));
        play.setOnAction(event -> {
            openFileOrFolder(row.getItem().getDownloadPath(), "stage.history.ctxmenu.play.error");
            event.consume();
        });

        MenuItem openFolder = new MenuItem(ctx.getLocalizedString("stage.history.ctxmenu.openfolder"));
        openFolder.setOnAction(event -> {
            Path downloadPath = row.getItem().getDownloadPath();
            Path folderPath = Files.isDirectory(downloadPath) ? downloadPath : downloadPath.getParent();
            openFileOrFolder(folderPath, "stage.history.ctxmenu.openfolder.error");
            event.consume();
        });

        MenuItem copyUrl = new MenuItem(ctx.getLocalizedString("stage.history.ctxmenu.copyurl"));
        copyUrl.setOnAction(event -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(row.getItem().getUrl());
            Clipboard.getSystemClipboard().setContent(content);
            event.consume();
        });

        ctxMenu.getItems().addAll(play, openFolder, copyUrl);

        ctxMenu.setOnShowing(event -> play.setVisible(Files.isRegularFile(row.getItem().getDownloadPath())));

        return ctxMenu;
    }

    private void playFile(Path path) {
        if (Files.isRegularFile(path)) {
            openFileOrFolder(path, "stage.history.ctxmenu.play.error");
        }
    }

    private void openFileOrFolder(Path path, String errorMsgKey) {
        try {
            // Avoid using Desktop from awt package to open files/folders as it's crap! It hangs or doesn't work on Linux and Mac
            String command = SystemUtils.IS_OS_WINDOWS ? "explorer" :
                    (SystemUtils.IS_OS_LINUX ? "xdg-open" : "open");
            Runtime.getRuntime().exec(new String[] {command, path.toString()});
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            Dialogs.error(errorMsgKey);
        }
    }

    private void handleClearHistoryBtnClick(ActionEvent e) {
        historyManager.clearHistory();
        historyTableView.setItems(null);
        e.consume();
    }

    @Override
    public void onVisibilityLost() {
        Set<TableColumnConfigModel> columnConfigModels = new HashSet<>();
        var columns = historyTableView.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            var c = columns.get(i);
            columnConfigModels.add(new TableColumnConfigModel(COLUMN_ID_MAP.get(c.getId()), i, c.getWidth(), c.getSortType()));
        }

        List<Integer> sortOrderList = historyTableView.getSortOrder().stream()
                .map(TableColumnBase::getId)
                .map(COLUMN_ID_MAP::get)
                .toList();

        var historyTableConfigProperty = ctx.getConfigRegistry().get(HistoryTableConfigProperty.class);
        historyTableConfigProperty.setValue(new TableConfigModel(sortOrderList, columnConfigModels));
        historyTableConfigProperty.save();
    }

    @Override
    public void onBeforeVisible() {
        historyManager.getHistoryItemsAsync()
                .thenAccept(items -> Platform.runLater(() -> {
                    historyTableView.setItems(FXCollections.observableList(items));
                    restoreTableViewStateFromConfig();
                }));
    }
}
