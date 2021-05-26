package com.github.engatec.vdl.controller.component.subscriptions;

import java.util.function.Consumer;

import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.ui.Icon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class SubscriptionItemComponentController extends HBox {

    private final Stage stage;
    private final Subscription subscription;
    private final Consumer<Subscription> onDeleteButtonClickListener;

    @FXML private Label titleLabel;
    @FXML private Label urlLabel;
    @FXML private Button deleteBtn;

    public SubscriptionItemComponentController(
            Stage stage,
            Subscription subscription,
            Consumer<Subscription> onDeleteButtonClickListener
    ) {
        this.stage = stage;
        this.subscription = subscription;
        this.onDeleteButtonClickListener = onDeleteButtonClickListener;
    }

    @FXML
    public void initialize() {
        titleLabel.setText(subscription.getName());
        urlLabel.setText(subscription.getUrl());
        deleteBtn.setGraphic(new ImageView(Icon.DELETE_SMALL.getImage()));
        deleteBtn.setOnAction(this::deleteButtonClickHandler);
    }

    private void deleteButtonClickHandler(ActionEvent event) {
        onDeleteButtonClickListener.accept(subscription);
        event.consume();
    }

    public Subscription getItem() {
        return subscription;
    }
}
