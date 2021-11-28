package com.github.engatec.vdl.ui;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.util.Svg;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class SvgIcons {

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
        Group infoIcon = SvgIcons.info();
        Svg.scale(infoIcon, 0.8);
        Tooltip tooltip = new Tooltip(ApplicationContext.INSTANCE.getLocalizedString(key));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(infoIcon, tooltip);
        return infoIcon;
    }
}
