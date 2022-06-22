package com.github.engatec.vdl.ui.scene.control.cell;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;

public class ProgressBarWithPercentTableCell<T> extends TableCell<T, Double> {

    private final ProgressBar progressBar;
    private final Label label;
    private final StackPane stackPane;

    private ObservableValue<Double> observable;

    public ProgressBarWithPercentTableCell() {
        getStyleClass().add("progress-bar-table-cell");

        progressBar = new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE);

        label = new Label();
        label.setStyle("-fx-font-weight: bold");

        stackPane = new StackPane(progressBar, label);
    }

    public static <T> Callback<TableColumn<T, Double>, TableCell<T, Double>> forTableColumn() {
        return param -> new ProgressBarWithPercentTableCell<T>();
    }

    @Override
    public void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
            return;
        }

        progressBar.progressProperty().unbind();
        label.textProperty().unbind();
        label.setText(StringUtils.EMPTY);

        final TableColumn<T, Double> column = getTableColumn();
        observable = column == null ? null : column.getCellObservableValue(getIndex());

        if (observable != null) {
            progressBar.progressProperty().bind(observable);
            label.textProperty().bind(Bindings.createStringBinding(() -> convertDoubleToPercentString(observable.getValue()), observable));
        } else if (item != null) {
            progressBar.setProgress(item);
            label.setText(convertDoubleToPercentString(item));
        }

        setGraphic(stackPane);
    }

    private String convertDoubleToPercentString(Double value) {
        if (value == null || value < 0) {
            return StringUtils.EMPTY;
        }

        return (int) (value * 100) + "%";
    }
}
