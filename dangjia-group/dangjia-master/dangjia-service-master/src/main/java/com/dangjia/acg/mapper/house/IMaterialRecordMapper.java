package com.dangjia.acg.mapper.house;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.modle.house.MaterialRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * author: Ronalcheng
 * Date: 2019/4/10 0010
 * Time: 9:48
 */
@Repository
public interface IMaterialRecordMapper extends Mapper<MaterialRecord> {

    //更新商品名称
    void updateMaterialRecordById(@Param("lists") JSONArray lists, @Param("brandSeriesId") String brandSeriesId, @Param("brandId") String brandId,
                                  @Param("goodsId") String goodsId, @Param("id") String id);
}
