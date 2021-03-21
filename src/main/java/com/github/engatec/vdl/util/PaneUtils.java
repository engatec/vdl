package com.github.engatec.vdl.util;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class PaneUtils {

    public static Node fillAnchorPane(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        return node;
    }
}
