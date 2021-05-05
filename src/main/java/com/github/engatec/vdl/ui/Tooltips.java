package com.github.engatec.vdl.ui;

import com.github.engatec.vdl.core.I18n;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class Tooltips {

    public static Tooltip createNew(String textKey) {
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.millis(300));
        I18n.bindLocaleProperty(tooltip.textProperty(), textKey);
        return tooltip;
    }
}
