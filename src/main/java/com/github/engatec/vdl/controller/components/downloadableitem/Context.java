package com.github.engatec.vdl.controller.components.downloadableitem;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

class Context {

    private final Label titleLabel;
    private final Label durationLabel;
    private final ComboBox<String> formatsComboBox;
    private final Button allFormatsButton;

    Context(
            Label titleLabel,
            Label durationLabel,
            ComboBox<String> formatsComboBox,
            Button allFormatsButton
    ) {
        this.titleLabel = titleLabel;
        this.durationLabel = durationLabel;
        this.formatsComboBox = formatsComboBox;
        this.allFormatsButton = allFormatsButton;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }

    public Label getDurationLabel() {
        return durationLabel;
    }

    public ComboBox<String> getFormatsComboBox() {
        return formatsComboBox;
    }

    public Button getAllFormatsButton() {
        return allFormatsButton;
    }
}
