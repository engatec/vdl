package com.github.engatec.vdl.controller.history;

import java.nio.file.Path;

import com.github.engatec.vdl.model.HistoryItem;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

class Context {

    private final Stage stage;

    private final TableView<HistoryItem> historyTableView;
    private final TableColumn<HistoryItem, String> titleTableColumn;
    private final TableColumn<HistoryItem, String> urlTableColumn;
    private final TableColumn<HistoryItem, Path> locationTableColumn;
    private final TableColumn<HistoryItem, String> dtmTableColumn;
    private final ComboBox<Number> entriesNumberComboBox;
    private final Button clearHistoryBtn;
    private final Button closeBtn;

    Context(
            Stage stage,
            TableView<HistoryItem> historyTableView,
            TableColumn<HistoryItem, String> titleTableColumn,
            TableColumn<HistoryItem, String> urlTableColumn,
            TableColumn<HistoryItem, Path> locationTableColumn,
            TableColumn<HistoryItem, String> dtmTableColumn,
            ComboBox<Number> entriesNumberComboBox,
            Button clearHistoryBtn,
            Button closeBtn
    ) {
        this.stage = stage;
        this.historyTableView = historyTableView;
        this.titleTableColumn = titleTableColumn;
        this.urlTableColumn = urlTableColumn;
        this.locationTableColumn = locationTableColumn;
        this.dtmTableColumn = dtmTableColumn;
        this.entriesNumberComboBox = entriesNumberComboBox;
        this.clearHistoryBtn = clearHistoryBtn;
        this.closeBtn = closeBtn;
    }

    Stage getStage() {
        return stage;
    }

    TableView<HistoryItem> getHistoryTableView() {
        return historyTableView;
    }

    public TableColumn<HistoryItem, String> getTitleTableColumn() {
        return titleTableColumn;
    }

    public TableColumn<HistoryItem, String> getUrlTableColumn() {
        return urlTableColumn;
    }

    public TableColumn<HistoryItem, Path> getLocationTableColumn() {
        return locationTableColumn;
    }

    public TableColumn<HistoryItem, String> getDtmTableColumn() {
        return dtmTableColumn;
    }

    ComboBox<Number> getEntriesNumberComboBox() {
        return entriesNumberComboBox;
    }

    Button getClearHistoryBtn() {
        return clearHistoryBtn;
    }

    Button getCloseBtn() {
        return closeBtn;
    }
}
