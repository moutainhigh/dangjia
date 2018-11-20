package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.modle.matter.WorkerEveryday;
import com.dangjia.acg.modle.worker.WorkerDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**工人流水明细
 * zmj
 */
@Repository
public interface IWorkerDetailMapper extends Mapper<WorkerDetail> {
    Double getCountWorkerDetailByWid(@Param("workerId")String workerId);
    List<String> getHistoryMonth(@Param("workerId")String workerId);
    List<WorkerDetail> getHistoryMonthByWorkerId(@Param("workerId")String workerId,@Param("createDate")String createDate);
}

