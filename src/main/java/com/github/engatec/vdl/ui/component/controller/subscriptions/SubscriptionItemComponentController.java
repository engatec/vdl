package com.github.engatec.vdl.ui.component.controller.subscriptions;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.ui.Icon;
import com.github.engatec.vdl.ui.helper.Tooltips;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

public class SubscriptionItemComponentController extends HBox {

    private final Subscription subscription;
    private final Consumer<Subscription> onRefreshButtonClickListener;
    private final Consumer<Subscription> onDeleteButtonClickListener;

    @FXML private TextField urlTextField;
    @FXML private Button refreshBtn;
    @FXML private Button changeFolderBtn;
    @FXML private Button deleteBtn;

    public SubscriptionItemComponentController(
            Subscription subscription,
            Consumer<Subscription> onRefreshButtonClickListener,
            Consumer<Subscription> onDeleteButtonClickListener
    ) {
        this.subscription = subscription;
        this.onRefreshButtonClickListener = onRefreshButtonClickListener;
        this.onDeleteButtonClickListener = onDeleteButtonClickListener;
    }

    @FXML
    public void initialize() {
        urlTextField.setText(subscription.getUrl());

        refreshBtn.setGraphic(new ImageView(Icon.REFRESH_SMALL.getImage()));
        refreshBtn.setOnAction(this::handleRefreshButtonClick);
        refreshBtn.setTooltip(Tooltips.create("subscriptions.tooltip.refresh"));

        changeFolderBtn.setGraphic(new ImageView(Icon.FOLDER_SMALL.getImage()));
        changeFolderBtn.setOnAction(this::handleChangeFolderButtonClick);
        changeFolderBtn.setTooltip(Tooltips.create("subscriptions.tooltip.changefolder"));

        deleteBtn.setGraphic(new ImageView(Icon.DELETE_SMALL.getImage()));
        deleteBtn.setOnAction(this::handleDeleteButtonClick);
        deleteBtn.setTooltip(Tooltips.create("subscriptions.tooltip.delete"));
    }

    private void handleRefreshButtonClick(ActionEvent event) {
        onRefreshButtonClickListener.accept(subscription);
        event.consume();
    }

    private void handleChangeFolderButtonClick(ActionEvent event) {
        var directoryChooser = new DirectoryChooser();
        File recentDownloadPath = Path.of(subscription.getDownloadPath()).toFile();
        if (recentDownloadPath.isDirectory()) {
            directoryChooser.setInitialDirectory(recentDownloadPath);
        }
        File selectedDirectory = directoryChooser.showDialog(getScene().getWindow());
        if (selectedDirectory != null) {
            subscription.setDownloadPath(selectedDirectory.getAbsolutePath());
        }
        ApplicationContext.getInstance().getManager(SubscriptionsManager.class).update(subscription);

        event.consume();
    }

    private void handleDeleteButtonClick(ActionEvent event) {
        onDeleteButtonClickListener.accept(subscription);
        event.consume();
    }

    public Subscription getItem() {
        return subscription;
    }
}
