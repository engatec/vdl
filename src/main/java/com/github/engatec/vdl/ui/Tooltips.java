package com.github.engatec.vdl.ui;

import com.github.engatec.vdl.core.ApplicationContext;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class Tooltips {

    public static Tooltip createNew(String textKey) {
        String text = ApplicationContext.getInstance().getLocalizedString(textKey);
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(300));
        return tooltip;
    }
}
