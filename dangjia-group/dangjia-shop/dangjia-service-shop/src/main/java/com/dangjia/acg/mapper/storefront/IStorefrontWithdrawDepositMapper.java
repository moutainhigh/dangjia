package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;


/**
 * 提现申请
 * at ysl 2019/1/24  11:15
 */
@Repository
public interface IStorefrontWithdrawDepositMapper extends Mapper<WithdrawDeposit> {

    BankCard queryBankCard(@Param("bankCardNumber") String bankCardNumber, @Param("workerId") String workerId);

}
