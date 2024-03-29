package com.github.engatec.vdl.db.mapper;

import java.util.List;

import com.github.engatec.vdl.model.QueueItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QueueMapper {

    int insertQueueItems(List<QueueItem> items);

    List<QueueItem> fetchQueueItems();

    int deleteQueueItems(List<Long> ids);

    int insertQueueTempFile(@Param("queueId") Long queueId, @Param("filePath") String filePath);
}
