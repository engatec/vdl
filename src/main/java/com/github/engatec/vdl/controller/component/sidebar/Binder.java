package com.github.engatec.vdl.controller.component.sidebar;

import com.github.engatec.vdl.core.I18n;

class Binder {

    static void bind(Context ctx) {
        bindLocale(ctx);
    }

    private static void bindLocale(Context ctx) {
        I18n.bindLocaleProperty(ctx.getSearchLabel().textProperty(), "sidebar.search");
        I18n.bindLocaleProperty(ctx.getDownloadsLabel().textProperty(), "sidebar.downloads");
        I18n.bindLocaleProperty(ctx.getHistoryLabel().textProperty(), "sidebar.history");
    }
}
