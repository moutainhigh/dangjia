package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.dto.storefront.*;
import com.dangjia.acg.dto.supplier.AccountFlowRecordDTO;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.storefront.Storefront;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

@Repository
public interface IStorefrontAccountFlowRecordMapper extends Mapper<AccountFlowRecord> {

}
