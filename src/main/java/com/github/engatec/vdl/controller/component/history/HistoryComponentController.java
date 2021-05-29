package com.github.engatec.vdl.controller.component.history;

import java.nio.file.Path;
import java.util.ResourceBundle;

import com.github.engatec.vdl.controller.component.ComponentController;
import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.HistoryManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.HistoryItem;
import com.github.engatec.vdl.model.preferences.wrapper.misc.HistoryEntriesNumberPref;
import com.github.engatec.vdl.ui.Dialogs;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HistoryComponentController extends VBox implements ComponentController {

    private static final Logger LOGGER = LogManager.getLogger(HistoryComponentController.class);

    @FXML private TableView<HistoryItem> historyTableView;
    @FXML private TableColumn<HistoryItem, String> titleTableColumn;
    @FXML private TableColumn<HistoryItem, String> urlTableColumn;
    @FXML private TableColumn<HistoryItem, Path> locationTableColumn;
    @FXML private TableColumn<HistoryItem, String> dtmTableColumn;

    @FXML private Label entriesNumberLabel;
    @FXML private ComboBox<Integer> entriesNumberComboBox;
    @FXML private Button clearHistoryBtn;

    @FXML
    public void initialize() {
        initEntriesNumberComboBox();
        initHistoryTableView();

        clearHistoryBtn.setOnAction(this::handleClearHistoryBtnClick);
    }

    private void initEntriesNumberComboBox() {
        final String DISABLE_HISTORY = ApplicationContext.INSTANCE.getResourceBundle().getString("stage.history.combobox.disablehistory");

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

        entriesNumberComboBox.valueProperty().bindBidirectional(ConfigRegistry.get(HistoryEntriesNumberPref.class).getProperty().asObject());
    }

    private void initHistoryTableView() {
        historyTableView.setPlaceholder(new Label(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.history.table.placeholder")));

        titleTableColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getTitle()));
        urlTableColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getUrl()));
        locationTableColumn.setCellValueFactory(it -> new ReadOnlyObjectWrapper<>(it.getValue().getPath()));
        dtmTableColumn.setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getDtm()));

        historyTableView.setRowFactory(tableView -> {
            TableRow<HistoryItem> row = new TableRow<>();
            ContextMenu contextMenu = createContextMenu(row);
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty().not())
                            .then(contextMenu)
                            .otherwise((ContextMenu) null)
            );
            return row;
        });
    }

    private ContextMenu createContextMenu(TableRow<HistoryItem> row) {
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();
        ContextMenu ctxMenu = new ContextMenu();

        MenuItem openFolder = new MenuItem(resourceBundle.getString("stage.history.ctxmenu.openfolder"));
        openFolder.setOnAction(event -> {
            Path path = row.getItem().getPath();
            try {
                // Avoid using Desktop from awt package to open folders as it's crap! It hangs or doesn't work on Linux and Mac
                String command = SystemUtils.IS_OS_WINDOWS ? "explorer" :
                        (SystemUtils.IS_OS_LINUX ? "xdg-open" : "open");
                Runtime.getRuntime().exec(new String[] {command, path.toString()});
            } catch (Exception e) {
                LOGGER.warn(e.getMessage());
                Dialogs.error("stage.history.ctxmenu.openfolder.error");
            }
            event.consume();
        });
        ctxMenu.getItems().add(openFolder);

        return ctxMenu;
    }

    private void handleClearHistoryBtnClick(ActionEvent e) {
        HistoryManager.INSTANCE.clearHistory();
        historyTableView.setItems(null);
        e.consume();
    }

    @Override
    public void onBeforeVisible() {
        historyTableView.setItems(FXCollections.observableList(HistoryManager.INSTANCE.getHistoryItems()));
    }

    @Override
    public void onVisibilityLost() {
        AppExecutors.SYSTEM_EXECUTOR.submit(HistoryManager.INSTANCE::persist); // To eliminate possible phantom entries from the hard drive if the user starts playing with max entries number
    }
}
