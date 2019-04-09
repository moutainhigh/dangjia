package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.dto.finance.WebWithdrawDTO;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 提现申请
 * at ysl 2019/1/24  11:15
 */
@Repository
public interface IWithdrawDepositMapper extends Mapper<WithdrawDeposit> {

    List<WebWithdrawDTO> getWebWithdrawList(@Param("state") Integer state,
                                            @Param("searchKey") String searchKey,
                                            @Param("beginDate") String beginDate,
                                            @Param("endDate") String endDate);
}

