package com.github.engatec.vdl.handler;

import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.ScrollEvent;

public class ComboBoxRollingScrollHandler implements EventHandler<ScrollEvent> {

    @Override
    public void handle(ScrollEvent event) {
        if (event.getSource() instanceof ComboBox src) {
            SingleSelectionModel<?> selectionModel = src.getSelectionModel();
            if (event.getDeltaY() < 0) {
                selectionModel.selectNext();
            } else if (event.getDeltaY() > 0) {
                selectionModel.selectPrevious();
            }
        }
        event.consume();
    }
}
