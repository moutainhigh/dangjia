package com.dangjia.acg.mapper.activity;

import com.dangjia.acg.dto.activity.DjStoreParticipateActivitiesDTO;
import com.dangjia.acg.modle.activity.DjStoreParticipateActivities;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/18
 * Time: 21:17
 */
@Repository
public interface DjStoreParticipateActivitiesMapper extends Mapper<DjStoreParticipateActivities> {

    Integer queryRegistrationNumber(@Param("storeActivityId") String storeActivityId);

    List<DjStoreParticipateActivitiesDTO> queryParticipatingShopsList(@Param("id") String id,
                                                                      @Param("activityType") Integer activityType,
                                                                      @Param("activitySessionId") String activitySessionId);
}
