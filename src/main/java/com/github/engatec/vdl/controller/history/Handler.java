package com.github.engatec.vdl.controller.history;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.HistoryManager;
import javafx.event.ActionEvent;

class Handler {

    static void handleCloseBtnClick(Context ctx, ActionEvent e) {
        AppExecutors.BACKGROUND_EXECUTOR.submit(HistoryManager.INSTANCE::persistHistory); // To eliminate possible phantom entries from the hard drive if the user starts playing with max entries number
        ctx.getStage().close();
        e.consume();
    }

    static void handleClearHistoryBtnClick(Context ctx, ActionEvent e) {
        HistoryManager.INSTANCE.clearHistory();
        ctx.getHistoryTableView().setItems(null);
        e.consume();
    }
}
