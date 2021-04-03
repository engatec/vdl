package com.github.engatec.vdl.controller.history;

import java.nio.file.Path;

import com.github.engatec.vdl.model.HistoryItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class HistoryController {

    private final Stage stage;

    @FXML private TableView<HistoryItem> historyTableView;
    @FXML private TableColumn<HistoryItem, String> titleTableColumn;
    @FXML private TableColumn<HistoryItem, String> urlTableColumn;
    @FXML private TableColumn<HistoryItem, Path> locationTableColumn;
    @FXML private TableColumn<HistoryItem, String> dtmTableColumn;

    @FXML private ComboBox<Number> entriesNumberComboBox;
    @FXML private Button clearHistoryBtn;
    @FXML private Button closeBtn;

    public HistoryController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        var ctx = new Context(
                stage,
                historyTableView,
                titleTableColumn,
                urlTableColumn,
                locationTableColumn,
                dtmTableColumn,
                entriesNumberComboBox,
                clearHistoryBtn,
                closeBtn
        );

        Initializer.initialize(ctx);
        Binder.bind(ctx);
    }
}
