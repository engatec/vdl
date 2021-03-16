package com.github.engatec.vdl.ui;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.util.Svg;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class Icons {

    public static Group info() {
        return info("#000000");
    }

    public static Group info(String rgb) {
        return Svg.create(
                Svg.pathBuilder().d("M0 0h24v24H0z").build(),
                Svg.pathBuilder()
                        .d("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z")
                        .fill(rgb)
                        .build()
        );
    }

    public static Group infoWithTooltip(String key) {
        Group infoIcon = Icons.info();
        Svg.scale(infoIcon, 0.8);
        Tooltip tooltip = new Tooltip(ApplicationContext.INSTANCE.getResourceBundle().getString(key));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(infoIcon, tooltip);
        return infoIcon;
    }

    public static Group download() {
        return download("#000000");
    }

    public static Group download(String rgb) {
        return Svg.create(
                Svg.pathBuilder().d("M0 0h24v24H0z").build(),
                Svg.pathBuilder().d("M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z").fill(rgb).build()
        );
    }

    public static Group queue() {
        return queue("#000000");
    }

    public static Group queue(String rgb) {
        return Svg.create(
                Svg.pathBuilder().d("M0 0h24v24H0z").build(),
                Svg.pathBuilder()
                        .d("M4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm16-4H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-1 9h-4v4h-2v-4H9V9h4V5h2v4h4v2z")
                        .fill(rgb)
                        .build()
        );
    }
}
