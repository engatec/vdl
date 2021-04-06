package com.github.engatec.vdl.controller.history;

import java.nio.file.Path;
import java.util.ResourceBundle;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Initializer {

    private static final Logger LOGGER = LogManager.getLogger(Initializer.class);

    static void initialize(Context ctx) {
        ctx.getStage().setOnCloseRequest(Handler::handleCloseRequest);

        initEntriesNumberComboBox(ctx.getEntriesNumberComboBox());
        initHistoryTableView(ctx);

        ctx.getClearHistoryBtn().setOnAction(e -> Handler.handleClearHistoryBtnClick(ctx, e));
        ctx.getCloseBtn().setOnAction(e -> Handler.handleCloseBtnClick(ctx, e));
    }

    private static void initEntriesNumberComboBox(ComboBox<Number> entriesNumberComboBox) {
        final String DISABLE_HISTORY = ApplicationContext.INSTANCE.getResourceBundle().getString("stage.history.combobox.disablehistory");

        entriesNumberComboBox.setItems(FXCollections.observableArrayList(0, 10, 30, 50, 100, 1000));
        entriesNumberComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                if (object == null) {
                    return null;
                }

                int value = object.intValue();

                if (value == 0) {
                    return DISABLE_HISTORY;
                }

                return String.valueOf(object);
            }

            @Override
            public Number fromString(String string) {
                if (DISABLE_HISTORY.equals(string)) {
                    return 0;
                }

                return Integer.valueOf(string);
            }
        });

        entriesNumberComboBox.valueProperty().bindBidirectional(ConfigRegistry.get(HistoryEntriesNumberPref.class).getProperty());
    }

    private static void initHistoryTableView(Context ctx) {
        TableView<HistoryItem> historyTableView = ctx.getHistoryTableView();
        historyTableView.setPlaceholder(new Label(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.history.table.placeholder")));

        ctx.getTitleTableColumn().setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getTitle()));
        ctx.getUrlTableColumn().setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getUrl()));
        ctx.getLocationTableColumn().setCellValueFactory(it -> new ReadOnlyObjectWrapper<>(it.getValue().getPath()));
        ctx.getDtmTableColumn().setCellValueFactory(it -> new ReadOnlyStringWrapper(it.getValue().getDtm()));

        historyTableView.setItems(FXCollections.observableList(HistoryManager.INSTANCE.getHistoryItems()));
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

    private static ContextMenu createContextMenu(TableRow<HistoryItem> row) {
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
                LOGGER.warn(e.getMessage(), e);
                Dialogs.error("stage.history.ctxmenu.openfolder.error");
            }
            event.consume();
        });
        ctxMenu.getItems().add(openFolder);

        return ctxMenu;
    }
}
