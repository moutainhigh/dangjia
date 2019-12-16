package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.dto.supervisor.JFRewardPunishRecordDTO;
import com.dangjia.acg.dto.supervisor.PatrolRecordDTO;
import com.dangjia.acg.dto.supervisor.PatrolRecordIndexDTO;
import com.dangjia.acg.dto.supervisor.WorkerRewardPunishRecordDTO;
import com.dangjia.acg.dto.worker.RewardPunishRecordDTO;
import com.dangjia.acg.modle.worker.RewardPunishRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IRewardPunishRecordMapper extends Mapper<RewardPunishRecord> {
    List<RewardPunishRecordDTO> queryRewardPunishRecord(RewardPunishRecordDTO rewardPunishRecordDTO);

    RewardPunishRecordDTO getRewardPunishRecord(@Param("rewardPunishRecordId") String rewardPunishRecordId);

    List<WorkerRewardPunishRecordDTO>  queryRewardPunishRecordBykeyWord(@Param("keyWord") String keyWord,@Param("type") String type);

    JFRewardPunishRecordDTO  queryRewardPunishRecordDetail(@Param("id") String id);

    List<PatrolRecordIndexDTO>  getSupHomePage(@Param("memberId")String memberId,@Param("keyWord")String keyWord);
}