package com.github.engatec.vdl.core;

import java.util.ArrayList;
import java.util.List;

import com.github.engatec.vdl.model.QueueItem;

public class QueueManager {

    public static final QueueManager INSTANCE = new QueueManager();

    private final List<QueueItem> queueItems = new ArrayList<>();

    private QueueManager() {
    }

    public void addItem(QueueItem item) {
        queueItems.add(item);
    }

    public List<QueueItem> getQueueItems() {
        return queueItems;
    }
}
