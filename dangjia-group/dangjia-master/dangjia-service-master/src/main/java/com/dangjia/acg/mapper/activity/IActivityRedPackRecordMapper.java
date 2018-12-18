package com.dangjia.acg.mapper.activity;

import com.dangjia.acg.dto.activity.ActivityRedPackRecordDTO;
import com.dangjia.acg.modle.activity.ActivityRedPackRecord;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: qiyuxiang
 * Date: 2018/11/13 0031
 * Time: 17:01
 */
@Repository
public interface IActivityRedPackRecordMapper extends Mapper<ActivityRedPackRecord> {

    List<ActivityRedPackRecordDTO> queryActivityRedPackRecords(ActivityRedPackRecord activityRedPackRecord);
}
