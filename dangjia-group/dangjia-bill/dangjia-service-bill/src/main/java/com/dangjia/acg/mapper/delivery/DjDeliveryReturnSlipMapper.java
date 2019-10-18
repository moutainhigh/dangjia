package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.delivery.*;
import com.dangjia.acg.modle.delivery.DjDeliveryReturnSlip;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 10:38
 */
@Repository
public interface DjDeliveryReturnSlipMapper extends Mapper<DjDeliveryReturnSlip> {

    /**
     * 供货任务全部
     * @param supId
     * @param searchKey
     * @return
     */
    List<DjDeliveryReturnSlipDTO> querySupplyTaskList(@Param("supId") String supId,
                                                      @Param("searchKey") String searchKey,
                                                      @Param("cityId") String cityId);

    /**
     * 供货任务发货
     * @param supId
     * @param searchKey
     * @param invoiceStatus
     * @return
     */
    List<DjDeliveryReturnSlipDTO> querySupplyDeliverTaskList(@Param("supId") String supId,
                                                             @Param("searchKey") String searchKey,
                                                             @Param("invoiceStatus") String invoiceStatus,
                                                             @Param("cityId") String cityId);

    /**
     * 供货任务退货
     * @param supId
     * @param searchKey
     * @param invoiceStatus
     * @return
     */
    List<DjDeliveryReturnSlipDTO> querySupplyRepairTaskList(@Param("supId") String supId,
                                                            @Param("searchKey") String searchKey,
                                                            @Param("invoiceStatus") String invoiceStatus,
                                                            @Param("cityId") String cityId);




    int setDeliveryTask(@Param("id") String id,
                        @Param("invoiceType") Integer invoiceType,
                        @Param("shippingState") Integer shippingState);

    List<SupplierSettlementManagementDTO> querySupplierSettlementManagement(@Param("supId") String supId,
                                                                            @Param("applyState") Integer applyState,
                                                                            @Param("cityId") String cityId);

    List<DjDeliveryReturnSlipDTO> querySupplierSettlementList(@Param("supId") String supId,
                                                              @Param("shopId") String shopId,
                                                              @Param("applyState") Integer applyState);

    List<BuyersDimensionDTO> queryBuyersDimensionList(@Param("supId") String supId,
                                                      @Param("searchKey") String searchKey,
                                                      @Param("cityId") String cityId);

    List<BuyersDimensionDetailsDTO> queryBuyersDimensionDetailList(@Param("supId") String supId,
                                                                   @Param("houseId") String houseId,
                                                                   @Param("searchKey") String searchKey,
                                                                   @Param("cityId") String cityId);

    List<BuyersDimensionDTO> querySupplyDimensionList(@Param("supId") String supId,
                                                      @Param("productId") String productId,
                                                      @Param("cityId") String cityId);

    List<SupplierStoreDimensionDTO> querySupplierStoreDimensionList(@Param("supId") String supId,
                                                                    @Param("shopId") String shopId,
                                                                    @Param("cityId") String cityId);
}
