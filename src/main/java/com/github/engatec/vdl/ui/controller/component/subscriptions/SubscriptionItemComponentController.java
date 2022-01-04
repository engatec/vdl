package com.github.engatec.vdl.ui.controller.component.subscriptions;

import java.util.function.Consumer;

import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.ui.Icon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class SubscriptionItemComponentController extends HBox {

    private final Subscription subscription;
    private final Consumer<Subscription> onRefreshButtonClickListener;
    private final Consumer<Subscription> onDeleteButtonClickListener;

    @FXML private Label titleLabel;
    @FXML private Label urlLabel;
    @FXML private Button refreshBtn;
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
        titleLabel.setText(subscription.getName());
        urlLabel.setText(subscription.getUrl());

        refreshBtn.setGraphic(new ImageView(Icon.REFRESH_SMALL.getImage()));
        refreshBtn.setOnAction(this::handleRefreshButtonClick);

        deleteBtn.setGraphic(new ImageView(Icon.DELETE_SMALL.getImage()));
        deleteBtn.setOnAction(this::handleDeleteButtonClick);
    }

    private void handleRefreshButtonClick(ActionEvent event) {
        onRefreshButtonClickListener.accept(subscription);
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
