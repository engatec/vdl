package com.github.engatec.vdl.db.mapper;

import java.util.List;

import com.github.engatec.vdl.db.mapper.entity.QueueItemEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QueueMapper {

    int insertQueueItem(List<QueueItemEntity> items);
}
