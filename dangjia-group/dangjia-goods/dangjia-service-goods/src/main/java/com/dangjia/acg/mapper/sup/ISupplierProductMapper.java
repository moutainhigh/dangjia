package com.dangjia.acg.mapper.sup;

import com.dangjia.acg.modle.sup.SupplierProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 供应商关联货号
 */
@Repository
public interface ISupplierProductMapper extends Mapper<SupplierProduct> {

    /**根据供应商id,货品id*/
    SupplierProduct getSupplierProduct(@Param("supplierId")String supplierId,@Param("productId")String productId);

    List<SupplierProduct> querySupplierProduct(@Param("productId")String productId);
}
