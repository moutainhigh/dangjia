package com.dangjia.acg.mapper.pay;

import com.dangjia.acg.modle.pay.PurchaseOrder;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface IPurchaseOrderMapper extends Mapper<PurchaseOrder> {
}
