package com.dangjia.acg.mapper.matter;

import com.dangjia.acg.dto.matter.RenovationManualDTO;
import com.dangjia.acg.modle.matter.RenovationManual;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 装修指南
 * zmj
 */
@Repository
public interface IRenovationManualMapper extends Mapper<RenovationManual> {
    List<RenovationManual> getRenovationManualByWorkertyId(String workertyid);

    List<RenovationManualDTO> getStrategyList(@Param("workerTypeIds") String[] workerTypeIds);
}

