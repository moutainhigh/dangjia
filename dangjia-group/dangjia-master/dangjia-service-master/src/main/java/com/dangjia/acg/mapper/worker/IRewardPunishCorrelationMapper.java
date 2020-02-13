package com.dangjia.acg.mapper.worker;

import com.dangjia.acg.dto.worker.CraftsmenListDTO;
import com.dangjia.acg.dto.worker.RewardPunishCorrelationDTO;
import com.dangjia.acg.dto.worker.RewardPunishRecordDetailDTO;
import com.dangjia.acg.dto.worker.RewardPunishRecordListDTO;
import com.dangjia.acg.modle.worker.RewardPunishCorrelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

@Repository
public interface IRewardPunishCorrelationMapper extends Mapper<RewardPunishCorrelation> {
    List<RewardPunishCorrelationDTO> queryCorrelation(@Param("name") String name, @Param("type") Integer type);

    List<Map<String, Object>> queryCorrelationList(@Param("type") String type);

    List<CraftsmenListDTO> queryCraftsmenList(@Param("houseId") String houseId);

    List<RewardPunishRecordListDTO> queryPunishRecordList(@Param("houseId") String houseId);

    RewardPunishRecordDetailDTO queryPunishRecordDetailList(@Param("id") String id);
}