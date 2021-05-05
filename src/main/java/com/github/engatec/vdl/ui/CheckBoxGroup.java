package com.github.engatec.vdl.ui;

import java.util.List;
import java.util.function.Consumer;

import com.sun.javafx.collections.TrackableObservableList;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;

public class CheckBoxGroup {

    private final CheckBox root;
    private final ChangeListener<Boolean> rootSelectedChangeListener;
    private final ChangeListener<Boolean> childSelectedChangeListener;
    private final ObservableList<CheckBox> checkBoxes = new TrackableObservableList<>() {
        @Override
        protected void onChanged(Change<CheckBox> change) {
            while (change.next()) {
                for (CheckBox checkBox : change.getRemoved()) {
                    checkBox.selectedProperty().removeListener(childSelectedChangeListener);
                }

                updateSelection(change.getList());
            }
        }
    };

    private Consumer<Long> onSelectionUpdateListener;

    public CheckBoxGroup(CheckBox root) {
        childSelectedChangeListener = (observable, oldValue, newValue) -> updateSelection(checkBoxes);
        rootSelectedChangeListener = (observable, oldValue, newValue) -> {
            if (newValue != null) {
                for (CheckBox checkBox : checkBoxes) {
                    checkBox.selectedProperty().removeListener(childSelectedChangeListener);
                    checkBox.setSelected(newValue);
                    checkBox.selectedProperty().addListener(childSelectedChangeListener);
                }
                updateSelection(checkBoxes);
            }
        };
        root.selectedProperty().addListener(rootSelectedChangeListener);
        this.root = root;
    }

    private void updateSelection(List<CheckBox> list) {
        boolean allSelected = list.stream().allMatch(CheckBox::isSelected);
        root.selectedProperty().removeListener(rootSelectedChangeListener);
        root.setSelected(allSelected);
        root.selectedProperty().addListener(rootSelectedChangeListener);
        if (onSelectionUpdateListener != null) {
            long selectedCount = list.stream().filter(CheckBox::isSelected).count();
            onSelectionUpdateListener.accept(selectedCount);
        }
    }

    public void add(CheckBox checkBox) {
        checkBox.selectedProperty().addListener(childSelectedChangeListener);
        checkBoxes.add(checkBox);
    }

    public void clear() {
        checkBoxes.clear();
    }

    public void setOnSelectionUpdateListener(Consumer<Long> onSelectionUpdateListener) {
        this.onSelectionUpdateListener = onSelectionUpdateListener;
    }
}
