package com.dangjia.acg.mapper.repair;

import com.dangjia.acg.dto.deliver.OrderSplitItemDTO;
import com.dangjia.acg.dto.house.WarehouseGoodsDTO;
import com.dangjia.acg.dto.repair.ReturnOrderProgressDTO;
import com.dangjia.acg.modle.repair.MendMateriel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface IMendMaterialMapper extends Mapper<MendMateriel> {

    List<MendMateriel> askAndQuit(@Param("workerTypeId") String workerTypeId, @Param("houseId") String houseId,
                                  @Param("categoryId") String categoryId, @Param("name") String name);

    List<MendMateriel> byMendOrderId(@Param("mendOrderId") String mendOrderId);

    MendMateriel getMendOrderGoods(@Param("mendOrderId") String mendOrderId, @Param("productId") String productId);

    List<WarehouseGoodsDTO> getWarehouseGoods(@Param("productId") String productId,@Param("houseId") String houseId);
    List<WarehouseGoodsDTO> getWarehouseWorker(@Param("productId") String productId,@Param("houseId") String houseId);

    int setStorefrontId(@Param("mendOrderId") String mendOrderId);


    List<ReturnOrderProgressDTO> queryMendMaterielProgress(@Param("progressOrderId") String progressOrderId);

    List<OrderSplitItemDTO> searchReturnRefundMaterielList(@Param("mendOrderId") String mendOrderId,@Param("mendDeliverId") String mendDeliverId);

    List<Map<String,Object>> getsupplierInfoList(@Param("storefrontId") String storefrontId, @Param("productId") String productId, @Param("houseId") String houseId);

    List<Map<String,Object>> selectSupMaterialByMendId(@Param("mendOrderId") String mendOrderId,@Param("mendDeliverId") String mendDeliverId);

    void updateMendDeliverIdByInfo(@Param("mendDeliverId") String mendDeliverId,@Param("mendOrderId") String mendOrderId,@Param("supplierId") String supplierId);
}