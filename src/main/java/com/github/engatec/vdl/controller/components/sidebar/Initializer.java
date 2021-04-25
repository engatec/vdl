package com.github.engatec.vdl.controller.components.sidebar;

import com.github.engatec.vdl.ui.Icon;
import javafx.scene.image.ImageView;

class Initializer {

    static void initialize(Context ctx) {
        initGraphic(ctx);
    }

    private static void initGraphic(Context ctx) {
        ctx.getSearchLabel().setGraphic(new ImageView(Icon.SEARCH_SMALL.getImage()));
        ctx.getDownloadsLabel().setGraphic(new ImageView(Icon.DOWNLOAD_SMALL.getImage()));
        ctx.getHistoryLabel().setGraphic(new ImageView(Icon.HISTORY_SMALL.getImage()));
    }
}
