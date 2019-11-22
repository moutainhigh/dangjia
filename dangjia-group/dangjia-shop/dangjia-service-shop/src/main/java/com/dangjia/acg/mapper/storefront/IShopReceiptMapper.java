package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.modle.receipt.Receipt;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
@Repository
public interface IShopReceiptMapper extends Mapper<Receipt> {
}
