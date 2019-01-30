package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.modle.worker.WorkerDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * 提现申请
 * at ysl 2019/1/24  11:15
 */
@Repository
public interface IWithdrawDepositMapper extends Mapper<WithdrawDeposit> {

    //查询所有提现申请  Integer state :  0未处理,1同意 2不同意(驳回)
    List<WithdrawDeposit> getAllWithdraw(@Param("state") Integer state, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    /**
     * 本周所有提现申请
     * @return List<SplitDeliver>
     */
    List<WithdrawDeposit> getAllCurWeek(@Param("state") Integer state, @Param("beginDate") String beginDate, @Param("endDate") String endDate);
}

