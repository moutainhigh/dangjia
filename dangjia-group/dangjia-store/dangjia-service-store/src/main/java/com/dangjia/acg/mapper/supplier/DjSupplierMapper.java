package com.dangjia.acg.mapper.supplier;

import com.dangjia.acg.dto.supplier.DjSupplierDTO;
import com.dangjia.acg.dto.supplier.SupplierLikeDTO;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:21
 */
@Repository
public interface DjSupplierMapper extends Mapper<DjSupplier> {

    List<Storefront> querySupplyList(@Param("supId") String supId,@Param("searchKey") String searchKey);
    List<DjSupplierDTO>  queryDjSupplierByShopID(@Param("keyWord") String keyWord, @Param("applicationStatus") String applicationStatus, @Param("shopId") String shopId);
    DjSupplierDTO  queryDJsupplierById( @Param("id") String id, @Param("shopId") String shopId);
    DjSupplier queryDjSupplierByPass(@Param("supplierId") String supplierId);
    DjSupplier querySingleDjSupplier(@Param("userId") String userId, @Param("cityId") String cityId);

    Double myWallet(@Param("supId") String supId);

    int setSurplusMoney();

    List<SupplierLikeDTO> queryLikeSupplier(@Param("searchKey") String searchKey);

}
