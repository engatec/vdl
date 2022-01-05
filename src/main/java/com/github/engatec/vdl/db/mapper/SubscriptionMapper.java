package com.github.engatec.vdl.db.mapper;

import java.util.List;
import java.util.Set;

import com.github.engatec.vdl.model.Subscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SubscriptionMapper {

    int insertSubscription(@Param("s") Subscription s);

    int insertProcessedItems(@Param("subscriptionId") Long subscriptionId, @Param("itemIds") Set<String> itemIds);

    List<Subscription> fetchSubscriptions();

    int updateSubscription(@Param("s") Subscription s);

    int deleteSubscription(@Param("id") Long id);
}
