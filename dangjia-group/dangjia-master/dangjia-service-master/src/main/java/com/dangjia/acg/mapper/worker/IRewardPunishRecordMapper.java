package com.dangjia.acg.mapper.worker;

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
}