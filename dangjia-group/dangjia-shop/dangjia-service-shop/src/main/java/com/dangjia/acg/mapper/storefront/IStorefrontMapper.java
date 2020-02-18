package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.dto.storefront.*;
import com.dangjia.acg.dto.supplier.AccountFlowRecordDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

@Repository
public interface IStorefrontMapper extends Mapper<Storefront> {

    List<StorefrontListDTO> querySupplierApplicationShopList(@Param("searchKey") String searchKey, @Param("supId") String supId,
                                                             @Param("applicationStatus") String applicationStatus, @Param("cityId") String cityId);

    List<Storefront> queryLikeSingleStorefront(@Param("searchKey") String searchKey);

    List<StorefrontListDTO>  querySupplierSelectionSupply(@Param("searchKey") String searchKey, @Param("supId") String supId, @Param("cityId") String cityId);

    Double myWallet(@Param("storefrontId") String storefrontId, @Param("date") Date date);

    List<WebSplitDeliverItemDTO> queryStoreSupplierSettlement(@Param("storefrontId") String storefrontId,@Param("searchKey") String searchKey);

    List<StoreExpenseRecordDTO> selectStoreExpenseRecord(@Param("orderNumber") String orderNumber,@Param("storefrontId") String storefrontId,@Param("orderId") String orderId);

    List<ExpenseRecordOrderDetailDTO> storeExpenseRecordOrderDetail(@Param("orderId") String orderId);

    List<StoreOrderSplitItemDTO>  queryStoreOrderSplitItem(@Param("storefrontId") String storefrontId,@Param("houseId") String houseId,@Param("productId") String productId);

    List<StoreSplitDeliverDTO> queryStoreSplitDeliverDetail(@Param("orderSplitId") String orderSplitId);

    List<StoreRevenueRecordDTO> queryStoreRevenueRecord(@Param("storefrontId") String storefrontId,@Param("orderNumber") String orderNumber);

    List<AccountFlowRecordDTO> storeAccountFlowRecordDTO(@Param("storefrontId") String storefrontId, @Param("houseOrderId") String houseOrderId);


    List<StoreRepairMendOrderDTO> queryMendOrder(@Param("storefrontId") String storefrontId,@Param("mendOrderId") String mendOrderId);

    List<StoreRepairMendOrderDetailDTO> queryMendOrderDetail(@Param("mendOrderId") String mendOrderId);

    void setStorefrontSurplusMoney(@Param("cityId") String cityId);

    List<String> selectCityList();
    //根据店铺类型，查询店铺信息
    public Storefront selectShopStoreByTypeCityId(@Param("cityId") String cityId, @Param("storefrontType") String storefrontType);
}
