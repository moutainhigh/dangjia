package com.dangjia.acg.mapper.account;

import com.dangjia.acg.dto.supplier.IStoreAccountFlowRecordDTO;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IStoreAccountFlowRecordMapper extends Mapper<AccountFlowRecord> {

    List<IStoreAccountFlowRecordDTO> queryExpenditure(@Param("supId") String supId);
}
