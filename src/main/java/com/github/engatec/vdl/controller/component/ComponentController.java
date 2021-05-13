package com.github.engatec.vdl.controller.component;

public interface ComponentController {

    default void onBeforeVisible() {
    }

    default void onVisibilityLost() {
    }
}
