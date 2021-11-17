package com.github.engatec.vdl.db.mapper;

import java.nio.file.Path;
import java.util.List;

import com.github.engatec.vdl.DbManagerTestHelper;
import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.db.entity.FindQueueTempFilesResult;
import com.github.engatec.vdl.model.DownloadStatus;
import com.github.engatec.vdl.model.QueueItem;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QueueMapperTest {

    private static DbManager dbManager;

    @BeforeAll
    static void setUp() {
        dbManager = DbManagerTestHelper.DB_MANAGER;
    }

    private QueueItem createQueueItem() {
        var item = new QueueItem();
        item.setTitle("Title");
        item.setFormatId("FormatId");
        item.setBaseUrl("https://url");
        item.setDownloadPath(Path.of("~/Downloads/"));
        item.setSize("30Mb");
        item.setProgress(0.8);
        item.setStatus(DownloadStatus.IN_PROGRESS);
        return item;
    }

    @Test
    void insertQueueItems_shouldInsert() {
        QueueItem item = createQueueItem();
        assertThat(item.getId()).isNull();
        dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueItems(List.of(item))).join();
        assertThat(item.getId()).isNotNull();
    }

    @Test
    void fetchQueueItems_shouldFindMoreThanOne() {
        QueueItem it1 = createQueueItem();
        dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueItems(List.of(it1)))
                .thenRun(() -> dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueTempFile(it1.getId(), "~/Downloads/it1.mp4"))
                                .thenRun(() -> dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueTempFile(it1.getId(), "~/Downloads/it1.m4a"))).join()
                ).join();

        QueueItem it2 = createQueueItem();
        dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueItems(List.of(it2))).join();

        List<QueueItem> dbItems = dbManager.doQueryAsync(QueueMapper.class, QueueMapper::fetchQueueItems).join();
        assertThat(dbItems)
                .hasSizeGreaterThan(1)
                .extracting(QueueItem::getId)
                .contains(it1.getId(), it2.getId());

        for (QueueItem dbItem : dbItems) {
            if (dbItem.getId().equals(it2.getId())) {
                assertThat(dbItem.getDestinationsForTraversal()).isEmpty();
            }

            if (dbItem.getId().equals(it1.getId())) {
                assertThat(dbItem.getTitle()).isEqualTo(it1.getTitle());
                assertThat(dbItem.getFormatId()).isEqualTo(it1.getFormatId());
                assertThat(dbItem.getBaseUrl()).isEqualTo(it1.getBaseUrl());
                assertThat(dbItem.getDownloadPath()).isEqualTo(it1.getDownloadPath());
                assertThat(dbItem.getDestinationsForTraversal()).hasSize(2);
            }
        }
    }

    @Test
    void deleteQueueItems_shouldDeleteQueueItemAndTempFilesInfo() {
        QueueItem it1 = createQueueItem();
        dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueItems(List.of(it1)))
                .thenRun(() -> dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueTempFile(it1.getId(), "~/Downloads/it1.mp4"))
                        .thenRun(() -> dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueTempFile(it1.getId(), "~/Downloads/it1.m4a"))).join()
                ).join();

        QueueItem it2 = createQueueItem();
        dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueItems(List.of(it2)))
                .thenRun(() -> dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueTempFile(it2.getId(), "~/Downloads/it2.webm")))
                .join();

        List<QueueItem> dbItems = dbManager.doQueryAsync(QueueMapper.class, QueueMapper::fetchQueueItems).join();
        assertThat(dbItems)
                .extracting(QueueItem::getId)
                .contains(it1.getId(), it2.getId());

        List<FindQueueTempFilesResult> tempFilesResultBeforeDelete = dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.findQueueTempFiles(List.of(it1.getId(), it2.getId()))).join();
        assertThat(tempFilesResultBeforeDelete)
                .hasSize(3)
                .extracting(FindQueueTempFilesResult::getQueueId)
                .contains(it1.getId(), it2.getId());

        Integer deletedQueueItemEntries = dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.deleteQueueItems(List.of(it1.getId()))).join();
        assertThat(deletedQueueItemEntries).isEqualTo(1);

        List<QueueItem> dbItemsAfterDelete = dbManager.doQueryAsync(QueueMapper.class, QueueMapper::fetchQueueItems).join();
        assertThat(dbItemsAfterDelete)
                .extracting(QueueItem::getId)
                .doesNotContain(it1.getId());

        List<FindQueueTempFilesResult> tempFilesResultAfterDelete = dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.findQueueTempFiles(List.of(it1.getId(), it2.getId()))).join();
        assertThat(tempFilesResultAfterDelete)
                .hasSize(1)
                .extracting(FindQueueTempFilesResult::getQueueId)
                .contains(it2.getId())
                .doesNotContain(it1.getId());
    }

    @Test
    void insertQueueTempFile_insertionShouldFail_foreignKeyNull() {
        assertThatThrownBy(() -> dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueTempFile(null, "~/Downloads/abc.mp4")).join())
                .hasCauseInstanceOf(PersistenceException.class)
                .hasRootCauseInstanceOf(SQLiteException.class)
                .hasMessageContaining("SQLITE_CONSTRAINT_NOTNULL");
    }

    @Test
    void insertQueueTempFile_insertionShouldFail_foreignKeyError() {
        assertThatThrownBy(() -> dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueTempFile(10000L, "~/Downloads/abc.mp4")).join())
                .hasCauseInstanceOf(PersistenceException.class)
                .hasRootCauseInstanceOf(SQLiteException.class)
                .hasMessageContaining("SQLITE_CONSTRAINT_FOREIGNKEY");
    }

    @Test
    void insertQueueTempFile_shouldInsert() {
        QueueItem item = createQueueItem();
        dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueItems(List.of(item)))
                .thenRun(() -> dbManager.doQueryAsync(QueueMapper.class, mapper -> mapper.insertQueueTempFile(item.getId(), "~/Downloads/abc.mp4")))
                .join();
    }
}
