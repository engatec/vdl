package com.github.engatec.vdl.controller.stage;

import java.util.Iterator;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.controller.component.ComponentController;
import com.github.engatec.vdl.controller.component.DownloadsComponentController;
import com.github.engatec.vdl.controller.component.ServicebarComponentController;
import com.github.engatec.vdl.controller.component.SidebarComponentController;
import com.github.engatec.vdl.controller.component.history.HistoryComponentController;
import com.github.engatec.vdl.controller.component.search.SearchComponentController;
import com.github.engatec.vdl.controller.component.subscriptions.SubscriptionsComponentController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.ui.component.DownloadsComponent;
import com.github.engatec.vdl.ui.component.HistoryComponent;
import com.github.engatec.vdl.ui.component.ServicebarComponent;
import com.github.engatec.vdl.ui.component.SidebarComponent;
import com.github.engatec.vdl.ui.component.search.SearchComponent;
import com.github.engatec.vdl.ui.component.subscriptions.SubscriptionsComponent;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController extends StageAwareController {

    private final QueueManager queueManager = ApplicationContext.INSTANCE.getManager(QueueManager.class);

    @FXML private HBox rootNode;
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
        SubscriptionsManager.INSTANCE.setSubscriptionsUpdateProgressListener(sidebar.getSubscriptionsUpdateProgressListener());
    }

    private <T extends Node & ComponentController> void loadComponent(T component) {
        ObservableList<Node> contentPaneItems = contentPane.getChildren();
        for (Iterator<Node> it = contentPaneItems.iterator(); it.hasNext();) {
            ((ComponentController) it.next()).onVisibilityLost();
            it.remove();
        }

        component.onBeforeVisible();
        contentPaneItems.add(component);
    }

    private void initServiceBar() {
        ServicebarComponentController servicebar = new ServicebarComponent(stage).load();
        servicebarPane.getChildren().add(servicebar);
    }
}
