package com.github.engatec.vdl.controller.stage.subscriptions;

import java.util.List;
import java.util.ResourceBundle;

import com.github.engatec.fxcontrols.FxTextField;
import com.github.engatec.vdl.controller.component.subscriptions.PlaylistItemComponentController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.handler.textformatter.NotBlankTextFormatter;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.component.subscriptions.PlaylistItemComponent;
import com.github.engatec.vdl.validation.InputForm;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

public class PlaylistContentsController implements InputForm {

    private Stage stage;
    private List<VideoInfo> videoInfoList;

    @FXML private VBox contentVBox;

    @FXML private FxTextField subscriptionNameTextField;
    @FXML private Button subscribeBtn;
    @FXML private Button closeBtn;

    public PlaylistContentsController() {
    }

    public PlaylistContentsController(Stage stage, List<VideoInfo> videoInfoList) {
        this.stage = stage;
        this.videoInfoList = videoInfoList;
    }

    @FXML
    public void initialize() {
        subscriptionNameTextField.setTextFormatter(new NotBlankTextFormatter());
        subscriptionNameTextField.textProperty().addListener((observable, oldValue, newValue) -> subscriptionNameTextField.clearError());
        subscribeBtn.setOnAction(this::handleSubscribeButtonClick);
        closeBtn.setOnAction(this::handleCloseButtonClick);

        initContent();
    }

    private void initContent() {
        ObservableList<Node> contentList = contentVBox.getChildren();
        for (VideoInfo vi : videoInfoList) {
            PlaylistItemComponentController node = new PlaylistItemComponent(stage, vi).load();
            contentList.add(node);
        }
    }

    @Override
    public boolean hasErrors() {
        boolean hasErrors = false;
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        if (StringUtils.isBlank(subscriptionNameTextField.getText())) {
            subscriptionNameTextField.setError(resourceBundle.getString("field.error.mandatory"));
            hasErrors = true;
        }

        return hasErrors;
    }

    private void handleSubscribeButtonClick(ActionEvent event) {
        if (hasErrors()) {
            event.consume();
            return;
        }

        subscribe();
        stage.close();
        event.consume();
    }

    private void subscribe() {
        throw new NotImplementedException();
    }

    private void handleCloseButtonClick(ActionEvent event) {
        stage.close();
        event.consume();
    }
}
