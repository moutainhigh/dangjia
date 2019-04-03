package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.Warehouse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IWarehouseMapper extends Mapper<Warehouse> {

    List<String> categoryIdList(@Param("houseId")String houseId);
    Warehouse getByProductId(@Param("productId")String productId, @Param("houseId")String houseId);

    List<Warehouse> warehouseList(@Param("houseId")String houseId, @Param("categoryId")String categoryId,
                                  @Param("name")String name);

    /**服务类商品*/
    List<Warehouse> serverList(@Param("houseId")String houseId, @Param("categoryId")String categoryId,
                               @Param("name")String name);

    /**材料类商品*/
    List<Warehouse> materialsList(@Param("houseId")String houseId, @Param("categoryId")String categoryId,
                               @Param("name")String name);
    Double getHouseGoodsPrice(@Param("houseId")String houseId, @Param("name")String name);

}
