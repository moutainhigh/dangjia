package com.dangjia.acg.mapper.product;

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
public interface IGoodsDjStoreActivityMapper extends Mapper<DjStoreActivity> {

    List<Map> querySpellDeals(@Param("storeActivityProductId") String storeActivityProductId);
}
