package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.modle.worker.WorkerDetail;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 提现申请
 * at ysl 2019/1/24  11:15
 */
@Repository
public interface IWithdrawDepositMapper extends Mapper<WithdrawDeposit> {

    //查询所有提现申请
    List<WithdrawDeposit> getAllWithdraw();

}

