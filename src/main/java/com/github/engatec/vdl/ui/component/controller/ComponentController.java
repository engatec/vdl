package com.github.engatec.vdl.ui.component.controller;

public interface ComponentController {

    default void onBeforeVisible() {
    }

    default void onVisibilityLost() {
    }
}
