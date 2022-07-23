package com.github.engatec.vdl.ui.stage.core;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public abstract class ResizedOnStartupStage extends AppStage {

    @Override
    protected void init() {
        super.init();
        stage.setResizable(false);
        stage.setOnShown(event -> {
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            boolean stageResized = false;

            double maxScreenWidth = screenBounds.getWidth() / 1.5;
            if (stage.getWidth() > maxScreenWidth) {
                stage.setWidth(maxScreenWidth);
                stageResized = true;
            }

            double maxScreenHeight = screenBounds.getHeight() / 1.5;
            if (stage.getHeight() > maxScreenHeight) {
                stage.setHeight(maxScreenHeight);
                stage.setWidth(stage.getWidth() + 30);
                stageResized = true;
            }

            if (stageResized) {
                stage.centerOnScreen();
            }
        });
    }
}
