package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.dto.supplier.DjSupplierDeliverDTO;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * author: Ronalcheng
 * Date: 2018/12/5 0005
 * Time: 14:25
 */
@Repository
public interface IStorefrontSplitDeliverMapper extends Mapper<SplitDeliver> {

    //查询结算清单
    List<DjSupplierDeliverDTO> selectItemListbyReceiptNumber(@Param("receiptNumber") String receiptNumber);
}
