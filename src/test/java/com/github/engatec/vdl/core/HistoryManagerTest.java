package com.github.engatec.vdl.core;

import java.nio.file.Path;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.downloadable.BaseDownloadable;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.preferences.wrapper.misc.HistoryEntriesNumberPref;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class HistoryManagerTest {

    @BeforeAll
    static void setUp() {
        ConfigRegistry configRegistryMock = Mockito.mock(ConfigRegistry.class);
        ApplicationContext.INSTANCE.setConfigRegistry(configRegistryMock);
    }

    @Test
    void shouldAddToHistory() {
        HistoryEntriesNumberPref historyEntriesNumberPrefMock = Mockito.mock(HistoryEntriesNumberPref.class);

        ConfigRegistry configRegistryMock = ApplicationContext.INSTANCE.getConfigRegistry();
        Mockito.when(configRegistryMock.get(HistoryEntriesNumberPref.class)).thenReturn(historyEntriesNumberPrefMock);
        Mockito.when(historyEntriesNumberPrefMock.getValue()).thenReturn(5);

        assertThat(HistoryManager.INSTANCE.getHistoryItems()).isEmpty();

        Downloadable downloadable = new BaseDownloadable();
        downloadable.setTitle("Title");
        downloadable.setBaseUrl("http://localhost");
        downloadable.setDownloadPath(Path.of("/"));
        HistoryManager.INSTANCE.addToHistory(downloadable);

        assertThat(HistoryManager.INSTANCE.getHistoryItems()).hasSize(1);
    }

    @Test
    void shouldNotAddToHistory() {
        HistoryEntriesNumberPref historyEntriesNumberPrefMock = Mockito.mock(HistoryEntriesNumberPref.class);

        ConfigRegistry configRegistryMock = ApplicationContext.INSTANCE.getConfigRegistry();
        Mockito.when(configRegistryMock.get(HistoryEntriesNumberPref.class)).thenReturn(historyEntriesNumberPrefMock);
        Mockito.when(historyEntriesNumberPrefMock.getValue()).thenReturn(0);

        assertThat(HistoryManager.INSTANCE.getHistoryItems()).isEmpty();

        Downloadable downloadable = new BaseDownloadable();
        downloadable.setTitle("Title");
        downloadable.setBaseUrl("http://localhost");
        downloadable.setDownloadPath(Path.of("/"));
        HistoryManager.INSTANCE.addToHistory(downloadable);

        assertThat(HistoryManager.INSTANCE.getHistoryItems()).isEmpty();
    }
}
