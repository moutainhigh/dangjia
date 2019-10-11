package com.dangjia.acg.mapper.supplier;

import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

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
}
