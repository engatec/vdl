package com.github.engatec.vdl.controller;

import java.util.List;

import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.command.DownloadCommand;
import com.github.engatec.vdl.core.command.EnqueueCommand;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.util.AppUtils;
import com.github.engatec.vdl.util.Svg;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public abstract class AbstractDownloadGridController {

    protected HBox createButtonPane(Button... buttons) {
        HBox hBox = new HBox();
        hBox.setSpacing(8);
        hBox.getChildren().addAll(buttons);
        return hBox;
    }

    protected Button createDownloadButton(Stage parentStage, Downloadable downloadable, List<Postprocessing> postprocessingList) {
        Group svg = Svg.create(
                Svg.pathBuilder().d("M0 0h24v24H0z").build(),
                Svg.pathBuilder().d("M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z").fill("#000000").build()
        );

        Button downloadBtn = new Button();
        downloadBtn.setOnAction(e -> {
            downloadable.setPostprocessingSteps(postprocessingList);
            AppUtils.executeCommandResolvingPath(parentStage, new DownloadCommand(parentStage, downloadable), downloadable::setDownloadPath);
            e.consume();
        });

        initButtonLookAndFeel(downloadBtn, svg, "download");

        return downloadBtn;
    }

    protected Button createAddToQueueButton(Stage parentStage, Downloadable downloadable, List<Postprocessing> postprocessingList) {
        Group svg = Svg.create(
                Svg.pathBuilder().d("M0 0h24v24H0z").build(),
                Svg.pathBuilder()
                        .d("M4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm16-4H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-1 9h-4v4h-2v-4H9V9h4V5h2v4h4v2z")
                        .fill("#000000")
                        .build()
        );

        Button addToQueueBtn = new Button();
        addToQueueBtn.setOnAction(e -> {
            downloadable.setPostprocessingSteps(postprocessingList);
            AppUtils.executeCommandResolvingPath(parentStage, new EnqueueCommand(downloadable), downloadable::setDownloadPath);
            e.consume();
        });

        initButtonLookAndFeel(addToQueueBtn, svg, "component.downloadgrid.queue.add");

        return addToQueueBtn;
    }

    private void initButtonLookAndFeel(Button btn, Group svg, String localeKey) {
        Svg.scale(svg, 0.7);
        btn.setGraphic(svg);
        btn.getStyleClass().add("img-btn");

        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.millis(300));
        btn.setTooltip(tooltip);
        I18n.bindLocaleProperty(tooltip.textProperty(), localeKey);
    }
}
