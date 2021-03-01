package com.github.engatec.vdl.controller;

import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.ui.Icons;
import com.github.engatec.vdl.util.Svg;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public abstract class AbstractDownloadGridController {

    protected HBox createButtonPane(Button... buttons) {
        HBox hBox = new HBox();
        hBox.setSpacing(8);
        hBox.getChildren().addAll(buttons);
        return hBox;
    }

    protected Button createDownloadButton() {
        Group svg = Icons.download();
        Button downloadBtn = new Button();
        initButtonLookAndFeel(downloadBtn, svg, "download");
        return downloadBtn;
    }

    protected Button createAddToQueueButton() {
        Group svg = Icons.queue();
        Button addToQueueBtn = new Button();
        initButtonLookAndFeel(addToQueueBtn, svg, "component.downloadgrid.queue.add");
        return addToQueueBtn;
    }

    private void initButtonLookAndFeel(Button btn, Group svg, String localeKey) {
        Svg.scale(svg, 0.7);
        btn.setGraphic(svg);
        btn.getStyleClass().add("img-btn");

        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.millis(300));
        btn.setTooltip(tooltip);
        I18n.bindLocaleProperty(tooltip.textProperty(), localeKey);
    }
}
