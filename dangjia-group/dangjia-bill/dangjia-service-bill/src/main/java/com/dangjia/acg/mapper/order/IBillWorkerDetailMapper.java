package com.dangjia.acg.mapper.order;

import com.dangjia.acg.dto.finance.WebWorkerDetailDTO;
import com.dangjia.acg.modle.worker.WorkerDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 工人流水明细
 * zmj
 */
@Repository
public interface IBillWorkerDetailMapper extends Mapper<WorkerDetail> {


}

