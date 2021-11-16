package com.github.engatec.vdl.db.mapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.model.HistoryItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HistoryMapperTest {

    private static final Path DB_PATH = Path.of("test.db");
    private static DbManager dbManager;

    @BeforeAll
    static void setUp() throws IOException {
        Files.deleteIfExists(DB_PATH);
        dbManager = new DbManager(DB_PATH);
    }

    @Test
    void insertAndFetchHistoryItems_shouldInsertAndThenFetch() {
        String title = "insertHistoryItemsTest";

        List<HistoryItem> dbResultBeforeInsert = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbResultBeforeInsert)
                .extracting(HistoryItem::getTitle)
                .doesNotContain(title);

        HistoryItem item = new HistoryItem(title, "https://url", Path.of("~/Downloads/"));
        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.insertHistoryItems(List.of(item))).join();

        List<HistoryItem> dbResultAfterInsert = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbResultAfterInsert)
                .extracting(HistoryItem::getTitle)
                .contains(title);

        assertThat(dbResultAfterInsert)
                .extracting(HistoryItem::getCreatedAt)
                .isNotNull()
                .isNotEmpty();
    }

    @Test
    void clearHistory_shouldDeleteAll() {
        int count = 5;
        List<HistoryItem> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(new HistoryItem(String.valueOf(i), "https://url", Path.of("~/Downloads/")));
        }
        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.insertHistoryItems(items)).join();

        List<HistoryItem> dbResultBeforeClear = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbResultBeforeClear).hasSizeGreaterThanOrEqualTo(count);

        dbManager.doQueryAsync(HistoryMapper.class, HistoryMapper::clearHistory).join();
        List<HistoryItem> dbResultAfterClear = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbResultAfterClear).isEmpty();
    }

    @Test
    void stripHistory_shouldKeepNoMoreThanNEntries() {
        int maxEntries = 10;
        List<HistoryItem> items = new ArrayList<>();
        for (int i = 0; i < maxEntries * 3; i++) {
            items.add(new HistoryItem(String.valueOf(i), "https://url", Path.of("~/Downloads/")));
        }
        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.insertHistoryItems(items)).join();

        List<HistoryItem> dbResultBeforeStrip = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbResultBeforeStrip).hasSizeGreaterThanOrEqualTo(maxEntries * 3);

        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.stripHistory(maxEntries)).join();

        List<HistoryItem> dbResultAfterStrip = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbResultAfterStrip).hasSize(maxEntries);
    }

    @Test
    void stripHistory_shouldKeepLatestEntries() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime thresholdDtm = LocalDateTime.now();
        List<HistoryItem> items = List.of(
                new HistoryItem("title", "https://url", Path.of("~/Downloads/"), thresholdDtm.plusMinutes(5).format(formatter)),
                new HistoryItem("title", "https://url", Path.of("~/Downloads/"), thresholdDtm.plusDays(1).format(formatter)),
                new HistoryItem("title", "https://url", Path.of("~/Downloads/"), thresholdDtm.minusDays(1).format(formatter)),
                new HistoryItem("title", "https://url", Path.of("~/Downloads/"), thresholdDtm.minusMinutes(5).format(formatter)),
                new HistoryItem("title", "https://url", Path.of("~/Downloads/"), thresholdDtm.plusYears(1).format(formatter)),
                new HistoryItem("title", "https://url", Path.of("~/Downloads/"), thresholdDtm.minusYears(1).format(formatter))
        );

        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.insertHistoryItems(items)).join();
        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.stripHistory(3)).join();

        List<HistoryItem> dbItems = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbItems)
                .extracting(HistoryItem::getCreatedAt)
                .map(it -> LocalDateTime.parse(it, formatter))
                .allMatch(dtm -> dtm.isAfter(thresholdDtm));
    }

    @Test
    void stripHistory_shouldDeleteAll() {
        int totalEntries = 10;
        List<HistoryItem> items = new ArrayList<>();
        for (int i = 0; i < totalEntries; i++) {
            items.add(new HistoryItem(String.valueOf(i), "https://url", Path.of("~/Downloads/")));
        }
        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.insertHistoryItems(items)).join();

        List<HistoryItem> dbResultBeforeStrip = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbResultBeforeStrip).hasSizeGreaterThanOrEqualTo(totalEntries);

        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.stripHistory(0)).join();

        List<HistoryItem> dbResultAfterStrip = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbResultAfterStrip).isEmpty();
    }

    @Test
    void stripHistory_shouldRemainEmpty() {
        dbManager.doQueryAsync(HistoryMapper.class, HistoryMapper::clearHistory).join();
        List<HistoryItem> dbItems = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbItems).isEmpty();

        dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.stripHistory(0)).join();
        dbItems = dbManager.doQueryAsync(HistoryMapper.class, mapper -> mapper.fetchHistory(null)).join();
        assertThat(dbItems).isEmpty();
    }
}
