package com.github.engatec.vdl.controller.stage.subscriptions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.engatec.fxcontrols.FxDirectoryChooser;
import com.github.engatec.fxcontrols.FxTextField;
import com.github.engatec.vdl.controller.component.subscriptions.PlaylistItemComponentController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.handler.textformatter.NotBlankTextFormatter;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.preferences.wrapper.general.DownloadPathPref;
import com.github.engatec.vdl.ui.CheckBoxGroup;
import com.github.engatec.vdl.ui.component.subscriptions.PlaylistItemComponent;
import com.github.engatec.vdl.validation.InputForm;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class PlaylistContentsController implements InputForm {

    private Stage stage;
    private String playlistUrl;
    private List<VideoInfo> videoInfoList;

    @FXML private VBox contentVBox;

    @FXML private FxTextField subscriptionNameTextField;
    @FXML private FxDirectoryChooser subscriptionDownloadPathTextField;

    private CheckBoxGroup checkBoxGroup;
    @FXML private CheckBox selectAllCheckBox;

    @FXML private Button subscribeBtn;
    @FXML private Button closeBtn;

    public PlaylistContentsController() {
    }

    public PlaylistContentsController(Stage stage, String playlistUrl, List<VideoInfo> videoInfoList) {
        this.stage = stage;
        this.playlistUrl = playlistUrl;
        this.videoInfoList = videoInfoList;
    }

    @FXML
    public void initialize() {
        subscriptionNameTextField.setTextFormatter(new NotBlankTextFormatter());
        subscriptionNameTextField.textProperty().addListener((observable, oldValue, newValue) -> subscriptionNameTextField.clearError());
        subscriptionDownloadPathTextField.setButtonText(ApplicationContext.INSTANCE.getResourceBundle().getString("button.directorychoose"));
        subscriptionDownloadPathTextField.setPath(ConfigRegistry.get(DownloadPathPref.class).getValue());
        subscribeBtn.setOnAction(this::handleSubscribeButtonClick);
        closeBtn.setOnAction(this::handleCloseButtonClick);

        checkBoxGroup = new CheckBoxGroup(selectAllCheckBox);

        initContent();
    }

    private void initContent() {
        ObservableList<Node> contentList = contentVBox.getChildren();
        for (VideoInfo vi : videoInfoList) {
            PlaylistItemComponentController node = new PlaylistItemComponent(stage, vi).load();
            checkBoxGroup.add(node.getCheckbox());
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
        Set<String> processedItems = contentVBox.getChildren().stream()
                .map(it -> (PlaylistItemComponentController) it)
                .filter(PlaylistItemComponentController::isSelected)
                .map(PlaylistItemComponentController::getItem)
                .map(it -> StringUtils.firstNonBlank(it.getId(), it.getUrl(), it.getTitle()))
                .collect(Collectors.toSet());

        var subscription = new Subscription();
        subscription.setName(subscriptionNameTextField.getText());
        subscription.setPlaylistUrl(playlistUrl);
        subscription.setProcessedItems(processedItems);
        subscription.setCreatedAt(LocalDateTime.now());
        SubscriptionsManager.INSTANCE.subscribe(subscription);
    }

    private void handleCloseButtonClick(ActionEvent event) {
        stage.close();
        event.consume();
    }
}
