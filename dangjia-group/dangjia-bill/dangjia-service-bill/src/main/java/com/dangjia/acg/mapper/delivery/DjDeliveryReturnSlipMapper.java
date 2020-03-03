package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.dto.delivery.*;
import com.dangjia.acg.modle.delivery.DjDeliveryReturnSlip;
import com.dangjia.acg.modle.storefront.Storefront;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
                                                             @Param("invoiceStatus") Integer invoiceStatus,
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
                                                            @Param("invoiceStatus") Integer invoiceStatus,
                                                            @Param("cityId") String cityId);




    int setDeliveryTask(@Param("id") String id,
                        @Param("invoiceType") Integer invoiceType,
                        @Param("shippingState") Integer shippingState);

    List<SupplierSettlementManagementDTO> querySupplierSettlementManagement(@Param("supId") String supId,
                                                                            @Param("applyState") Integer applyState,
                                                                            @Param("cityId") String cityId,
                                                                            @Param("storefronts") List<Storefront> storefronts);

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


    List<BuyersDimensionDetailsDTO> querySupplierStoreDimensionDetailList(@Param("supId") String supId,
                                                                          @Param("shopId") String shopId,
                                                                          @Param("cityId") String cityId,
                                                                          @Param("searchKey") String searchKey);


    /**
     * 店铺利润-买家维度
     * @param storefrontId
     * @param searchKey 地址/业主姓名
     * @return
     */
    List<StoreBuyersDimensionDTO> sellerDimension(@Param("storefrontId") String storefrontId,@Param("searchKey") String searchKey);


    List<SupplierDimensionSupplyDTO> supplierDimensionSupplyDetails(@Param("storefrontId") String storefrontId, @Param("supId") String supId, @Param("searchKey") String searchKey);


    List<SupplierDimensionOrderDetailDTO> supplierDimensionOrderDetails(@Param("houseId")  String houseId,@Param("storefrontId")  String storefrontId,@Param("supId")  String supId,@Param("addressId")  String addressId);

    List<SupplierDimensionGoodsDetailDTO> supplierDimensionGoodsDetails(@Param("splitDevlierId")  String splitDevlierId  );

    /**
     * 店铺利润-买家维度详情
     * @return
     */
    List<StoreBuyersDimensionDetailDTO> sellerDimensionDetail(@Param("houseId") String houseId);


    /**
     * 店铺利润-买家货单详情
     * @param orderSplitId
     * @return
     */
    List<StoreBuyersDimensionOrderDetailDTO> shippingDetails(@Param("orderSplitId") String orderSplitId);



    /**
     * 店铺利润统计-供应商维度
     * @param storefrontId
     * @return
     */
    List<StoreSupplierDimensionDTO>  supplierDimension (@Param("storefrontId") String storefrontId,
                                                        @Param("startTime") Date startTime,
                                                        @Param("endTime") Date endTime,
                                                        @Param("searchKey") String searchKey);


    /**
     * 店铺利润统计-商品维度
     * @param storefrontId
     * @param searchKey
     * @return
     */
    List<StoreSupplyDimensionDTO> storefrontProductDimension (@Param("storefrontId") String storefrontId, @Param("searchKey") String searchKey);


    /**
     * 店铺利润统计-商品维度详情
     * @return
     */
    List<StoreSupplyDimensionDetailDTO> storefrontProductDimensionDetail (@Param("storefrontId") String storefrontId, @Param("productId") String productId,@Param("cityId") String cityId);

    /**
     * 店铺利润统计-卖家维度-发货单详情
     * @return
     */

    List<StoreSellerSplitDeliverDetailsDTO> sellerSplitDeliverDetails (@Param("splitDeliverId") String splitDeliverId,@Param("storefrontId") String storefrontId );

    //利润统计--商品维度--发货单列表
    List<StoreBuyersDimensionOrderDetailDTO> storefrontProductDimensionSplitDetail(@Param("storefrontId") String storefrontId,@Param("productId") String productId,@Param("addressId") String addressId,@Param("houseId") String houseId);
    //利润统计--商品维度--退货单列表
    List<StoreBuyersDimensionOrderDetailDTO> storefrontProductDimensionMendDetail(@Param("storefrontId") String storefrontId,@Param("productId") String productId,@Param("addressId") String addressId,@Param("houseId") String houseId);
    //利润统计--商品维度--发货单总收入，支出
    Map<String,Object> totalProductDimensionSplitDetail(@Param("storefrontId") String storefrontId, @Param("productId") String productId,@Param("addressId") String addressId,@Param("houseId") String houseId);
    //利润统计--商品维度--退货单总收入，支出
    Map<String,Object> totalProductDimensionMendDetail(@Param("storefrontId") String storefrontId, @Param("productId") String productId,@Param("addressId") String addressId,@Param("houseId") String houseId);

}


