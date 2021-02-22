package com.github.engatec.vdl.util;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

public class Svg {

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
