package com.github.engatec.vdl.controller.components;

import java.util.List;
import java.util.function.BiFunction;

import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.downloadable.Video;
import com.github.engatec.vdl.worker.data.DownloadableData;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

public class DownloadableItemsComponentController {

    private List<DownloadableData> downloadableDataList;
    private BiFunction<List<Video>, List<Audio>, ? extends Parent> contentFunction;

    @FXML private VBox rootVBox;

    private DownloadableItemsComponentController() {
    }

    public DownloadableItemsComponentController(List<DownloadableData> downloadableDataList, BiFunction<List<Video>, List<Audio>, ? extends Parent> contentFunction) {
        this.downloadableDataList = downloadableDataList;
        this.contentFunction = contentFunction;
    }

    @FXML
    public void initialize() {
        rootVBox.setSpacing(4);

        boolean singleItem = downloadableDataList.size() == 1;
        for (DownloadableData item : downloadableDataList) {
            TitledPane tp = new TitledPane(item.getTitle(), contentFunction.apply(item.getVideoList(), item.getAudioList()));
            tp.setExpanded(singleItem);
            tp.setCollapsible(!singleItem);
            tp.getStyleClass().add("no-border");
            rootVBox.getChildren().add(tp);
        }
    }
}
