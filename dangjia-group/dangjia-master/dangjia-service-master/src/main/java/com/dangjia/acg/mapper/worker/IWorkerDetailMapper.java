package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.dto.finance.WebWorkerDetailDTO;
import com.dangjia.acg.modle.worker.WorkerDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 工人流水明细
 * zmj
 */
@Repository
public interface IWorkerDetailMapper extends Mapper<WorkerDetail> {
    /**
     * 总收入
     */
    Double incomeMoney(@Param("workerId") String workerId, @Param("time") String time,@Param("states") String[] states);

    /**
     * 总支出
     */
    Double outMoney(@Param("workerId") String workerId, @Param("time") String time,@Param("states") String[] states);

    List<WorkerDetail> incomeDetail(@Param("workerId") String workerId, @Param("time") String time);

    List<WorkerDetail> outDetail(@Param("workerId") String workerId, @Param("time") String time);


    List<Map> getHistoryMonth(@Param("workerId") String workerId, @Param("time") String time);

    List<WorkerDetail> getHistoryMonthByWorkerId(@Param("workerId") String workerId, @Param("createDate") String createDate);

    //所有流水
    List<WorkerDetail> getAllWallet(@Param("cityId") String cityId,@Param("workerId") String workerId, @Param("houseId") String houseId,

                                    @Param("likeMobile") String likeMobile, @Param("likeAddress") String likeAddress);

    /**
     * --         0每日完工  1阶段完工，
     * --         2整体完工  3巡查, 4验收,
     * --         8补人工, 9退人工, 10奖 11罚
     * @param houseId
     * @param workerId
     * @param workerType
     * @return
     */
    List<WebWorkerDetailDTO> getHouseWallet(@Param("houseId") String houseId, @Param("workerId") String workerId,@Param("workerType") String workerType);


}

