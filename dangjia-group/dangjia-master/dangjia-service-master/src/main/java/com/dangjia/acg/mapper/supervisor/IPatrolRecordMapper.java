package com.dangjia.acg.mapper.supervisor;

import com.dangjia.acg.dto.supervisor.PatrolRecordDTO;
import com.dangjia.acg.modle.supervisor.PatrolRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IPatrolRecordMapper extends Mapper<PatrolRecord> {

    List<PatrolRecordDTO> getPatrolRecordList(@Param("type") Integer type, @Param("searchKey") String searchKey);

    List<PatrolRecordDTO> getAppPatrolRecordList(@Param("type") Integer type, @Param("memberId") String memberId);

}
