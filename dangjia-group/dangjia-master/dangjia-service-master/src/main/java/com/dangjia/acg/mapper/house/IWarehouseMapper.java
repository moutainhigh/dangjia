package com.dangjia.acg.mapper.house;

import com.dangjia.acg.modle.house.Warehouse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IWarehouseMapper extends Mapper<Warehouse> {

    Warehouse getByProductId(@Param("productId")String productId, @Param("houseId")String houseId);

    List<Warehouse> warehouseList(@Param("houseId")String houseId, @Param("categoryId")String categoryId,
                                  @Param("name")String name);
}
