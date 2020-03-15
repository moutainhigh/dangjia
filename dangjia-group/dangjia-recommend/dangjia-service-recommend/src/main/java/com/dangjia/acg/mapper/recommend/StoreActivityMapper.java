package com.dangjia.acg.mapper.recommend;

import com.dangjia.acg.modle.activity.DjStoreActivity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商家活动
 */
@Repository
public interface StoreActivityMapper {

    /** 查询活动列表 */
    List<DjStoreActivity> queryList(@Param("activityType")Integer activityType, @Param("keyword")String keyword);

}
