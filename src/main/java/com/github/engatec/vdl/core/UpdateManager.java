package com.github.engatec.vdl.core;

import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.worker.UpdateBinariesTask;
import javafx.stage.Stage;

public class UpdateManager {

    public static void updateYoutubeDl(Stage owner) {
        String title = ApplicationContext.INSTANCE.getResourceBundle().getString("dialog.progress.title.label.updateinprogress");
        Dialogs.progress(title, owner, new UpdateBinariesTask());
    }
}
