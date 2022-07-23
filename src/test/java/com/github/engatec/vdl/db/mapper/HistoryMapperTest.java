package com.github.engatec.vdl.db.mapper;

import com.github.engatec.vdl.TestHelper;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.model.HistoryItem;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HistoryMapperTest {

    private static DbManager dbManager;

    @BeforeAll
    static void setUp() {
        TestHelper.initTestApplicationContext();
        dbManager = ApplicationContext.getInstance().getManager(DbManager.class);
    }

    @Test
    void insertAndFetchHistoryItems_shouldInsertAndThenFetch() {
        String title = "insertHistoryItemsTest";

        List<HistoryItem> dbResultBeforeInsert = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
        assertThat(dbResultBeforeInsert)
                .extracting(HistoryItem::getTitle)
                .doesNotContain(title);

        HistoryItem item = new HistoryItem(title, "https://url", Path.of("~/Downloads/"));
        dbManager.doQuery(HistoryMapper.class, mapper -> mapper.insertHistoryItems(List.of(item)));

        List<HistoryItem> dbResultAfterInsert = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
        assertThat(dbResultAfterInsert)
                .extracting(HistoryItem::getTitle)
                .contains(title);

        assertThat(dbResultAfterInsert)
                .extracting(HistoryItem::getCreatedAt)
                .allMatch(StringUtils::isNotBlank);
    }

    @Test
    void clearHistory_shouldDeleteAll() {
        int count = 5;
        List<HistoryItem> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            items.add(new HistoryItem(String.valueOf(i), "https://url", Path.of("~/Downloads/")));
        }
        dbManager.doQuery(HistoryMapper.class, mapper -> mapper.insertHistoryItems(items));

        List<HistoryItem> dbResultBeforeClear = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
        assertThat(dbResultBeforeClear).hasSizeGreaterThanOrEqualTo(count);

        dbManager.doQuery(HistoryMapper.class, HistoryMapper::clearHistory);
        List<HistoryItem> dbResultAfterClear = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
        assertThat(dbResultAfterClear).isEmpty();
    }

    @Test
    void stripHistory_shouldKeepNoMoreThanNEntries() {
        int maxEntries = 10;
        List<HistoryItem> items = new ArrayList<>();
        for (int i = 0; i < maxEntries * 3; i++) {
            items.add(new HistoryItem(String.valueOf(i), "https://url", Path.of("~/Downloads/")));
        }
        dbManager.doQuery(HistoryMapper.class, mapper -> mapper.insertHistoryItems(items));

        List<HistoryItem> dbResultBeforeStrip = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
        assertThat(dbResultBeforeStrip).hasSizeGreaterThanOrEqualTo(maxEntries * 3);

        dbManager.doQuery(HistoryMapper.class, mapper -> mapper.stripHistory(maxEntries));

        List<HistoryItem> dbResultAfterStrip = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
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

        dbManager.doQuery(HistoryMapper.class, mapper -> mapper.insertHistoryItems(items));
        dbManager.doQuery(HistoryMapper.class, mapper -> mapper.stripHistory(3));

        List<HistoryItem> dbItems = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
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
        dbManager.doQuery(HistoryMapper.class, mapper -> mapper.insertHistoryItems(items));

        List<HistoryItem> dbResultBeforeStrip = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
        assertThat(dbResultBeforeStrip).hasSizeGreaterThanOrEqualTo(totalEntries);

        dbManager.doQuery(HistoryMapper.class, mapper -> mapper.stripHistory(0));

        List<HistoryItem> dbResultAfterStrip = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
        assertThat(dbResultAfterStrip).isEmpty();
    }

    @Test
    void stripHistory_shouldRemainEmpty() {
        dbManager.doQuery(HistoryMapper.class, HistoryMapper::clearHistory);
        List<HistoryItem> dbItems = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
        assertThat(dbItems).isEmpty();

        dbManager.doQuery(HistoryMapper.class, mapper -> mapper.stripHistory(0));
        dbItems = dbManager.doQuery(HistoryMapper.class, mapper -> mapper.fetchHistory(null));
        assertThat(dbItems).isEmpty();
    }
}
