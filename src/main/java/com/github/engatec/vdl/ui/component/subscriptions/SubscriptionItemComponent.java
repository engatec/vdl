package com.github.engatec.vdl.ui.component.subscriptions;

import java.util.function.Consumer;

import com.github.engatec.vdl.controller.component.subscriptions.SubscriptionItemComponentController;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.ui.component.AppComponent;
import javafx.stage.Stage;

public class SubscriptionItemComponent extends AppComponent<SubscriptionItemComponentController> {

    private final Subscription subscription;
    private final Consumer<Subscription> onDeleteButtonClickListener;

    public SubscriptionItemComponent(Stage stage, Subscription subscription, Consumer<Subscription> onDeleteButtonClickListener) {
        super(stage);
        this.subscription = subscription;
        this.onDeleteButtonClickListener = onDeleteButtonClickListener;
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/subscriptions/subscription_item.fxml";
    }

    @Override
    protected SubscriptionItemComponentController getController() {
        return new SubscriptionItemComponentController(stage, subscription, onDeleteButtonClickListener);
    }
}
