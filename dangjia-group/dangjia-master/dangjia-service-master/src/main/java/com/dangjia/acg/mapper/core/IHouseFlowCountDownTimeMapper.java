package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.core.HouseFlow;
import com.dangjia.acg.modle.core.HouseFlowCountDownTime;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * 记录工匠每个单可抢单时间
 * zmj
 */
@Repository
public interface IHouseFlowCountDownTimeMapper extends Mapper<HouseFlowCountDownTime> {

}
