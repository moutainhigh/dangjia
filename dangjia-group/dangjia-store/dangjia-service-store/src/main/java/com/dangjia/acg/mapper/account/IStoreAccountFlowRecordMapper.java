package com.dangjia.acg.mapper.account;

import com.dangjia.acg.dto.supplier.AccountFlowRecordDTO;
import com.dangjia.acg.dto.supplier.IStoreAccountFlowRecordDTO;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IStoreAccountFlowRecordMapper extends Mapper<AccountFlowRecord> {

    List<AccountFlowRecordDTO> accountFlowRecordDTOs(@Param("supId") String supId,
                                                     @Param("houseOrderId") String houseOrderId);
}
