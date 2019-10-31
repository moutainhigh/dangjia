package com.dangjia.acg.mapper.delivery;

import com.dangjia.acg.modle.matter.TechnologyRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**验收工艺记录
 */
@Repository
public interface IBillTechnologyRecordMapper extends Mapper<TechnologyRecord> {

}

