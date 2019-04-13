package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.modle.worker.RewardPunishCondition;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IRewardPunishConditionMapper extends Mapper<RewardPunishCondition>{
}