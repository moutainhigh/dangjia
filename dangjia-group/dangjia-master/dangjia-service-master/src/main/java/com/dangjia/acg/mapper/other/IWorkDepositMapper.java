package com.dangjia.acg.mapper.other;

import com.dangjia.acg.modle.other.WorkDeposit;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**结算比例
 * zmj
 */
@Repository
public interface IWorkDepositMapper extends Mapper<WorkDeposit> {
}

