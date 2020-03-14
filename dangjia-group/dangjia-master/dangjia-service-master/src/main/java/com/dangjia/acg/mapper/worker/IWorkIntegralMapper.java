package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.dto.worker.WorkIntegralDTO;
import com.dangjia.acg.dto.worker.WorkerComprehensiveDTO;
import com.dangjia.acg.dto.worker.WorkerRunkDTO;
import com.dangjia.acg.modle.worker.WorkIntegral;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/11/27 0027
 * Time: 9:41
 */
@Repository
public interface IWorkIntegralMapper extends Mapper<WorkIntegral> {

    List<WorkIntegralDTO> queryWorkIntegral(@Param("workerId")String workerId);


    List<WorkerRunkDTO> querySoaringWorker(@Param("workerType") Integer workerType,@Param("startTime") String startTime, @Param("endTime")String endTime);


    List<WorkerRunkDTO> queryRankingWorker(@Param("workerType") Integer workerType);


    WorkerComprehensiveDTO getComprehensiveWorker(@Param("workerId")String workerId);


    /**
     * 查询当家贝流水明细
     * @param memberId
     * @return
     */
    List<WorkIntegralDTO> queryWorkerIntegerList(@Param("memberId")String memberId);

    Map<String,Object> getTotalShellMoney(@Param("memberId") String memberId);

}
