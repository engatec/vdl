package com.github.engatec.vdl.ui.helper;

import com.github.engatec.vdl.core.ApplicationContext;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
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
        Tooltip tooltip = new Tooltip(ApplicationContext.getInstance().getLocalizedString(key));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(infoIcon, tooltip);
        return infoIcon;
    }

    private static class Svg {

        public static Group create(Node... path) {
            return new Group(path);
        }

        public static void scale(Group svg, double scaleFactor) {
            svg.setScaleX(scaleFactor);
            svg.setScaleY(scaleFactor);
        }

        public static PathBuilder pathBuilder() {
            return new PathBuilder();
        }

        public static class PathBuilder {
            private String d;
            private String fill;

            private PathBuilder() {
            }

            public PathBuilder d(String value) {
                d = value;
                return this;
            }

            public PathBuilder fill(String rgb) {
                fill = rgb;
                return this;
            }

            public SVGPath build() {
                SVGPath path = new SVGPath();
                path.setContent(d);
                path.setFill(fill == null ? null : Color.web(fill));
                return path;
            }
        }
    }
}
