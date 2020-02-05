package com.dangjia.acg.mapper.supplier;

import com.dangjia.acg.dto.delivery.SupplyDimensionDTO;
import com.dangjia.acg.dto.supplier.DjSupSupplierProductDTO;
import com.dangjia.acg.modle.supplier.DjSupSupplierProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 11/10/2019
 * Time: 下午 1:53
 */
@Repository
public interface DjSupSupplierProductMapper extends Mapper<DjSupSupplierProduct> {

    List<DjSupSupplierProductDTO> querySupplierGoods(@Param("supId") String supId);

    List<DjSupSupplierProductDTO> queryHaveGoods(@Param("supId") String supId,
                                                 @Param("shopId") String shopId,
                                                 @Param("applicationStatus") String applicationStatus,
                                                 @Param("keyWord")String keyWord);

    List<DjSupSupplierProductDTO> searchHaveGoodsSize(@Param("supId") String supId,
                                                      @Param("shopId") String shopId,
                                                      @Param("applicationStatus") String applicationStatus);

    String queryAttributeNameByIds(@Param("ids") String [] ids);

    List<SupplyDimensionDTO> queryDjSupSupplierProductList(@Param("supId") String supId,
                                                           @Param("searchKey") String searchKey);

    List<DjSupSupplierProductDTO> queryNotForTheGoods(@Param("shopId") String shopId,
                                                      @Param("productIds") List<String> productIds);

}

