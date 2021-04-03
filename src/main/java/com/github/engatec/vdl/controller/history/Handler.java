package com.github.engatec.vdl.controller.history;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.HistoryManager;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

class Handler {

    static void handleCloseRequest(WindowEvent e) {
        AppExecutors.BACKGROUND_EXECUTOR.submit(HistoryManager.INSTANCE::persistHistory); // To eliminate possible phantom entries from the hard drive if the user starts playing with max entries number
    }

    static void handleCloseBtnClick(Context ctx, ActionEvent e) {
        Stage stage = ctx.getStage();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        e.consume();
    }

    static void handleClearHistoryBtnClick(Context ctx, ActionEvent e) {
        HistoryManager.INSTANCE.clearHistory();
        ctx.getHistoryTableView().setItems(null);
        e.consume();
    }
}
