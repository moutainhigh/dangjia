package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.modle.worker.WorkerBankCard;
import com.dangjia.acg.modle.worker.WorkerDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**银行卡绑定
 * zmj
 */
@Repository
public interface IWorkerBankCardMapper extends Mapper<WorkerBankCard> {
    List<WorkerBankCard> getByWorkerid(@Param("workerId")String workerId);
}

