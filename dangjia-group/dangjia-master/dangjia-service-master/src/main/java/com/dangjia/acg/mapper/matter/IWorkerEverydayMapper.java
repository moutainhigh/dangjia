package com.dangjia.acg.mapper.matter;

import com.dangjia.acg.modle.matter.WorkerEveryday;
import com.dangjia.acg.modle.member.Member;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**开工事项
 * zmj
 */
@Repository
public interface IWorkerEverydayMapper extends Mapper<WorkerEveryday> {
    List<WorkerEveryday> getWorkerEverydayList(@Param("type")int type);

}

