package com.dangjia.acg.mapper.product;

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
public interface IGoodsDjStoreParticipateActivitiesMapper extends Mapper<DjStoreParticipateActivities> {
}
