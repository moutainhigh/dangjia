package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.dto.house.WarehouseGoodsDTO;
import com.dangjia.acg.modle.repair.MendMateriel;
import com.dangjia.acg.modle.repair.MendWorker;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IMendMaterialMapper extends Mapper<MendMateriel> {

    List<MendMateriel> askAndQuit(@Param("workerTypeId") String workerTypeId, @Param("houseId") String houseId,
                                  @Param("categoryId") String categoryId, @Param("name") String name);

    List<MendMateriel> byMendOrderId(@Param("mendOrderId") String mendOrderId);

    MendMateriel getMendOrderGoods(@Param("mendOrderId") String mendOrderId, @Param("productId") String productId);

    List<WarehouseGoodsDTO> getWarehouseGoods(@Param("productId") String productId,@Param("houseId") String houseId);
    List<WarehouseGoodsDTO> getWarehouseWorker(@Param("productId") String productId,@Param("houseId") String houseId);

    MendWorker getMendWorker(@Param("productId") String productId,@Param("houseId") String houseId);
}