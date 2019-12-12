package com.dangjia.acg.mapper.supervisor;

import com.dangjia.acg.dto.supervisor.PatrolRecordDTO;
import com.dangjia.acg.modle.supervisor.DjBasicsPatrolRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface DjBasicsPatrolRecordMapper  extends Mapper<DjBasicsPatrolRecord> {

    PatrolRecordDTO  queryPatrolRecordDetail(@Param("rewordPunishCorrelationId")String rewordPunishCorrelationId);
}
