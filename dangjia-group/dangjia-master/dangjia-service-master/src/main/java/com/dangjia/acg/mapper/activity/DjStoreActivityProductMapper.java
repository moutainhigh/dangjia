package com.dangjia.acg.mapper.activity;

import com.dangjia.acg.dto.activity.DjStoreActivityProductDTO;
import com.dangjia.acg.modle.activity.DjStoreActivityProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/19
 * Time: 10:59
 */
@Repository
public interface DjStoreActivityProductMapper extends Mapper<DjStoreActivityProduct> {

    List<DjStoreActivityProductDTO> queryWaitingSelectionProduct(@Param("storefrontId") String storefrontId,
                                                                 @Param("storeActivityId")String storeActivityId,
                                                                 @Param("activitySessionId")String activitySessionId);

    List<DjStoreActivityProductDTO> querySelectedProduct(@Param("storefrontId") String storefrontId,
                                                         @Param("storeActivityId")String storeActivityId,
                                                         @Param("activitySessionId")String activitySessionId);
}
