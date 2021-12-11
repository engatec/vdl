package com.github.engatec.vdl.ui.controller.component;

public interface ComponentController {

    default void onBeforeVisible() {
    }

    default void onVisibilityLost() {
    }
}
