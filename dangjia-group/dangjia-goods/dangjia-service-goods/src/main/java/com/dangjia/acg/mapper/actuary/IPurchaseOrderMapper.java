package com.dangjia.acg.mapper.actuary;

import com.dangjia.acg.modle.actuary.PurchaseOrder;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IPurchaseOrderMapper extends Mapper<PurchaseOrder> {
}
