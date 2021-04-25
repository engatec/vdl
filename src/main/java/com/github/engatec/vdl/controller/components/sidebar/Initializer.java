package com.github.engatec.vdl.controller.components.sidebar;

import com.github.engatec.vdl.ui.Icons;
import javafx.scene.image.ImageView;

class Initializer {

    static void initialize(Context ctx) {
        initGraphic(ctx);
    }

    private static void initGraphic(Context ctx) {
        ctx.getSearchLabel().setGraphic(new ImageView(Icons.SEARCH_SMALL));
        ctx.getDownloadsLabel().setGraphic(new ImageView(Icons.DOWNLOAD_SMALL));
        ctx.getHistoryLabel().setGraphic(new ImageView(Icons.HISTORY_SMALL));
    }
}
