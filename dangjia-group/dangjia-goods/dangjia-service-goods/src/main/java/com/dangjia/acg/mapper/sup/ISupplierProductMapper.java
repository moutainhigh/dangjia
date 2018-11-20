package com.dangjia.acg.mapper.sup;

import com.dangjia.acg.modle.sup.SupplierProduct;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 供应商关联货号
 */
@Repository
public interface ISupplierProductMapper extends Mapper<SupplierProduct> {
}
