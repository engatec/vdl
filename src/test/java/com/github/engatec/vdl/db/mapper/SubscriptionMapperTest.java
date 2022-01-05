package com.github.engatec.vdl.db.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.github.engatec.vdl.TestHelper;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.model.Subscription;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SubscriptionMapperTest {

    private static DbManager dbManager;

    @BeforeAll
    static void setUp() {
        TestHelper.initTestApplicationContext();
        dbManager = ApplicationContext.getInstance().getManager(DbManager.class);
    }

    private Subscription createSubscriptionItem() {
        var item = new Subscription();
        item.setName("Name");
        item.setUrl("https://url");
        item.setDownloadPath("~/Downloads/");
        return item;
    }

    @Test
    void insertSubscription_shouldInsert() {
        var item = createSubscriptionItem();
        assertThat(item.getId()).isNull();
        dbManager.doQuery(SubscriptionMapper.class, mapper -> mapper.insertSubscription(item));
        assertThat(item.getId()).isNotNull();
    }

    @Test
    void fetchSubscriptions_shouldFindMoreThanOne() {
        Subscription it1 = createSubscriptionItem();
        dbManager.doQuery(SubscriptionMapper.class, mapper -> {
            mapper.insertSubscription(it1);
            mapper.insertProcessedItems(it1.getId(), Set.of("id1", "id2"));
            return 0;
        });

        Subscription it2 = createSubscriptionItem();
        dbManager.doQuery(SubscriptionMapper.class, mapper -> mapper.insertSubscription(it2));

        List<Subscription> dbItems = dbManager.doQuery(SubscriptionMapper.class, SubscriptionMapper::fetchSubscriptions);
        assertThat(dbItems)
                .hasSizeGreaterThan(1)
                .extracting(Subscription::getId)
                .contains(it1.getId(), it2.getId());

        for (Subscription dbItem : dbItems) {
            if (dbItem.getId().equals(it2.getId())) {
                assertThat(dbItem.getProcessedItemsForTraversal()).isEmpty();
            }

            if (dbItem.getId().equals(it1.getId())) {
                assertThat(dbItem.getName()).isEqualTo(it1.getName());
                assertThat(dbItem.getUrl()).isEqualTo(it1.getUrl());
                assertThat(dbItem.getDownloadPath()).isEqualTo(it1.getDownloadPath());
                assertThat(dbItem.getProcessedItemsForTraversal()).hasSize(2);
            }
        }
    }

    @Test
    void update_shouldUpdateSubscription() {
        Subscription s = createSubscriptionItem();
        dbManager.doQuery(SubscriptionMapper.class, mapper -> mapper.insertSubscription(s));

        String newPath = UUID.randomUUID().toString();
        assertThat(s.getDownloadPath()).isNotEqualTo(newPath);

        s.setDownloadPath(newPath);
        dbManager.doQuery(SubscriptionMapper.class, mapper -> mapper.updateSubscription(s));

        Subscription dbItem = dbManager.doQuery(SubscriptionMapper.class, SubscriptionMapper::fetchSubscriptions).stream()
                .filter(it -> it.getId().equals(s.getId()))
                .findFirst()
                .orElseThrow();
        assertThat(dbItem.getDownloadPath()).isEqualTo(newPath);
    }

    @Test
    void deleteSubscription_shouldDeleteSubscriptionAndProcessedItems() {
        Subscription it1 = createSubscriptionItem();
        dbManager.doQuery(SubscriptionMapper.class, mapper -> {
            mapper.insertSubscription(it1);
            mapper.insertProcessedItems(it1.getId(), Set.of("it1_id1", "it1_id2"));
            return 0;
        });

        Subscription it2 = createSubscriptionItem();
        dbManager.doQuery(SubscriptionMapper.class, mapper -> {
            mapper.insertSubscription(it2);
            mapper.insertProcessedItems(it2.getId(), Set.of("it2_id3"));
            return 0;
        });

        List<Subscription> dbItems = dbManager.doQuery(SubscriptionMapper.class, SubscriptionMapper::fetchSubscriptions);
        assertThat(dbItems)
                .extracting(Subscription::getId)
                .contains(it1.getId(), it2.getId());

        assertThat(dbItems)
                .filteredOn(it -> it.getId().equals(it1.getId()) || it.getId().equals(it2.getId()))
                .hasSize(2)
                .isSortedAccordingTo(Comparator.comparing(Subscription::getId))
                .extracting(it -> it.getProcessedItems().size())
                .containsSequence(2, 1);

        Integer deletedSubscriptionEntries = dbManager.doQuery(SubscriptionMapper.class, mapper -> mapper.deleteSubscription(it1.getId()));
        assertThat(deletedSubscriptionEntries).isEqualTo(1);

        List<Subscription> dbItemsAfterDelete = dbManager.doQuery(SubscriptionMapper.class, SubscriptionMapper::fetchSubscriptions);
        assertThat(dbItemsAfterDelete)
                .extracting(Subscription::getId)
                .doesNotContain(it1.getId());

        assertThat(dbItemsAfterDelete)
                .filteredOn(it -> it.getId().equals(it1.getId()) || it.getId().equals(it2.getId()))
                .hasSize(1)
                .allMatch(it -> it.getId().equals(it2.getId()))
                .extracting(it -> it.getProcessedItems().size())
                .containsSequence(1);
    }

    @Test
    void insertProcessedItems_insertionShouldFail_foreignKeyNull() {
        assertThatThrownBy(() -> dbManager.doQuery(SubscriptionMapper.class, mapper -> mapper.insertProcessedItems(null, Set.of("id1"))))
                .hasCauseInstanceOf(SQLiteException.class)
                .hasMessageContaining("SQLITE_CONSTRAINT_NOTNULL");
    }

    @Test
    void insertProcessedItems_insertionShouldFail_foreignKeyError() {
        assertThatThrownBy(() -> dbManager.doQuery(SubscriptionMapper.class, mapper -> mapper.insertProcessedItems(10000L, Set.of("id1"))))
                .hasCauseInstanceOf(SQLiteException.class)
                .hasMessageContaining("SQLITE_CONSTRAINT_FOREIGNKEY");
    }

    @Test
    void insertProcessedItems_shouldInsert() {
        Subscription item = createSubscriptionItem();
        dbManager.doQuery(SubscriptionMapper.class, mapper -> mapper.insertSubscription(item));
        dbManager.doQuery(SubscriptionMapper.class, mapper -> mapper.insertProcessedItems(item.getId(), Set.of("id1")));
    }
}
