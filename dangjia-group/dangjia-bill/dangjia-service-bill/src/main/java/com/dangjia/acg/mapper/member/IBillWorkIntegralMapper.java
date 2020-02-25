package com.dangjia.acg.mapper.member;

import com.dangjia.acg.dto.worker.WorkIntegralDTO;
import com.dangjia.acg.dto.worker.WorkerComprehensiveDTO;
import com.dangjia.acg.dto.worker.WorkerRunkDTO;
import com.dangjia.acg.modle.worker.WorkIntegral;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/27 0027
 * Time: 9:41
 */
@Repository
public interface IBillWorkIntegralMapper extends Mapper<WorkIntegral> {




}
