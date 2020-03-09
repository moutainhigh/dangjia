package com.dangjia.acg.mapper.activity;

import com.dangjia.acg.dto.activity.DjStoreActivityDTO;
import com.dangjia.acg.modle.activity.DjStoreActivity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/15
 * Time: 14:18
 */
@Repository
public interface DjStoreActivityMapper extends Mapper<DjStoreActivity> {

    List<DjStoreActivityDTO> queryActivitiesByStorefront(@Param("activityType") Integer activityType,
                                                         @Param("storefrontId") String storefrontId);

    List<DjStoreActivityDTO> queryActivitiesSessionByStorefront(@Param("id") String id,
                                                                @Param("storefrontId") String storefrontId);

    List<DjStoreActivityDTO>  queryActivitiesOrSessionById(@Param("id") String id,
                                                           @Param("activityType") Integer activityType);

    List<Map> querySpellDeals(@Param("storeActivityProductId") String storeActivityProductId);
}
