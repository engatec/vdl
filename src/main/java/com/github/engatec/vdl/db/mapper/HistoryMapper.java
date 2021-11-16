package com.github.engatec.vdl.db.mapper;

import java.util.List;

import com.github.engatec.vdl.model.HistoryItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HistoryMapper {

    int insertHistoryItems(List<HistoryItem> historyItems);

    List<HistoryItem> fetchHistory(@Param("limit") Integer limit);

    int clearHistory();

    int stripHistory(@Param("maxEntries") int maxEntries);
}
