package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.dto.worker.RewardPunishCorrelationDTO;
import com.dangjia.acg.modle.worker.RewardPunishCorrelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IRewardPunishCorrelationMapper extends Mapper<RewardPunishCorrelation> {
    List<RewardPunishCorrelationDTO> queryCorrelation(@Param("name") String name, @Param("state") Integer state);
}