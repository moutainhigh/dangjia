package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.dto.storefront.StoreExpenseRecordDTO;
import com.dangjia.acg.dto.storefront.StorefrontListDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

@Repository
public interface IStorefrontMapper extends Mapper<Storefront> {


    List<StorefrontListDTO> querySupplierApplicationShopList(@Param("searchKey") String searchKey,
                                                             @Param("supId") String supId,
                                                             @Param("applicationStatus") String applicationStatus,
                                                             @Param("cityId") String cityId);


    List<Storefront> queryLikeSingleStorefront(@Param("searchKey") String searchKey);


    List<StorefrontListDTO>  querySupplierSelectionSupply(@Param("searchKey") String searchKey,
                                                          @Param("supId") String supId,
                                                          @Param("cityId") String cityId);

    Double myWallet(@Param("storefrontId") String storefrontId, @Param("date") Date date);

    List<WebSplitDeliverItemDTO> queryStoreSupplierSettlement(@Param("storefrontId") String storefrontId,@Param("searchKey") String searchKey);
    List<StoreExpenseRecordDTO> selectStoreExpenseRecord(@Param("orderNumber") String orderNumber,@Param("storefrontId") String storefrontId);
}
