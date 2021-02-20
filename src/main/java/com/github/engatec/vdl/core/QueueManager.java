package com.github.engatec.vdl.core;

import java.util.LinkedList;

import com.github.engatec.vdl.model.QueueItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class QueueManager {

    public static final QueueManager INSTANCE = new QueueManager();

    private final ObservableList<QueueItem> queueItems = FXCollections.observableList(new LinkedList<>());

    private QueueManager() {
    }

    public void addItem(QueueItem item) {
        // Add most recent item rather then allow to have multiple same items in the queue
        queueItems.remove(item);
        queueItems.add(item);
    }

    public ObservableList<QueueItem> getQueueItems() {
        return queueItems;
    }
}
