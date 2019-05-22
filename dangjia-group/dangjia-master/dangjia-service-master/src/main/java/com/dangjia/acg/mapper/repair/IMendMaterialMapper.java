package com.dangjia.acg.mapper.repair;

import com.alibaba.fastjson.JSONArray;
import com.dangjia.acg.modle.repair.MendMateriel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IMendMaterialMapper extends Mapper<MendMateriel>{

    List<MendMateriel> askAndQuit(@Param("workerTypeId")String workerTypeId, @Param("houseId")String houseId,
                                              @Param("categoryId")String categoryId, @Param("name")String name);

    List<MendMateriel> byMendOrderId(@Param("mendOrderId") String mendOrderId);
    MendMateriel getMendOrderGoods(@Param("mendOrderId") String mendOrderId,@Param("productId") String productId);

    //更新商品名称
    void updateMendMaterialById(@Param("lists") JSONArray lists, @Param("brandSeriesId") String brandSeriesId, @Param("brandId") String brandId,
                                @Param("goodsId") String goodsId, @Param("id") String id);

}