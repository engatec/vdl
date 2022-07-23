package com.github.engatec.vdl.ui.stage.controller;

import java.util.Iterator;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.ui.component.controller.ComponentController;
import com.github.engatec.vdl.ui.component.controller.DownloadsComponentController;
import com.github.engatec.vdl.ui.component.controller.ServicebarComponentController;
import com.github.engatec.vdl.ui.component.controller.SidebarComponentController;
import com.github.engatec.vdl.ui.component.controller.history.HistoryComponentController;
import com.github.engatec.vdl.ui.component.controller.search.SearchComponentController;
import com.github.engatec.vdl.ui.component.controller.subscriptions.SubscriptionsComponentController;
import com.github.engatec.vdl.ui.component.core.DownloadsComponent;
import com.github.engatec.vdl.ui.component.core.HistoryComponent;
import com.github.engatec.vdl.ui.component.core.ServicebarComponent;
import com.github.engatec.vdl.ui.component.core.SidebarComponent;
import com.github.engatec.vdl.ui.component.core.search.SearchComponent;
import com.github.engatec.vdl.ui.component.core.subscriptions.SubscriptionsComponent;
import com.github.engatec.vdl.ui.data.UserDataType;
import com.github.engatec.vdl.ui.helper.StageUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController extends StageAwareController {

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final QueueManager queueManager = ctx.getManager(QueueManager.class);
    private final SubscriptionsManager subscriptionsManager = ctx.getManager(SubscriptionsManager.class);

    @FXML private StackPane navigationPane;
    @FXML private StackPane contentPane;
    @FXML private StackPane servicebarPane;

    private SearchComponentController search;
    private DownloadsComponentController downloads;
    private HistoryComponentController history;
    private SubscriptionsComponentController subscriptions;

    private MainController() {
    }

    public MainController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        initSideBar();
        initServiceBar();

        search = new SearchComponent(stage).load();
        downloads = new DownloadsComponent(stage).load();
        loadComponent(search);
    }

    private void initSideBar() {
        SidebarComponentController sidebar = new SidebarComponent(stage).load();
        sidebar.setOnSearchClickListener(() -> loadComponent(search));
        sidebar.setOnDownloadsClickListener(() -> loadComponent(downloads));
        sidebar.setOnHistoryClickListener(() -> {
            if (history == null) {
                history = new HistoryComponent(stage).load();
            }
            loadComponent(history);
        });
        sidebar.setOnSubscruptionsClickListener(() -> {
            if (subscriptions == null) {
                subscriptions = new SubscriptionsComponent(stage).load();
            }
            loadComponent(subscriptions);
        });

        navigationPane.getChildren().add(sidebar);
        queueManager.setOnQueueItemsChangeListener(sidebar.getOnQueueItemsChangeListener());
        subscriptionsManager.setSubscriptionsUpdateProgressListener(sidebar.getSubscriptionsUpdateProgressListener());
    }

    private <T extends Node & ComponentController> void loadComponent(T component) {
        ObservableList<Node> contentPaneItems = contentPane.getChildren();
        for (Iterator<Node> it = contentPaneItems.iterator(); it.hasNext();) {
            ((ComponentController) it.next()).onVisibilityLost();
            it.remove();
        }

        component.onBeforeVisible();
        contentPaneItems.add(component);

        // Storing active component as user data to make it possible to call onVisibilityLost method when user closes the app
        StageUtils.setUserData(stage, UserDataType.CURRENT_VISIBLE_COMPONENT, component);
    }

    private void initServiceBar() {
        ServicebarComponentController servicebar = new ServicebarComponent(stage).load();
        servicebarPane.getChildren().add(servicebar);
    }
}
