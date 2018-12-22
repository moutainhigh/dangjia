package com.dangjia.acg.mapper.matter;

import com.dangjia.acg.modle.matter.TechnologyRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**验收工艺记录
 * zmj
 */
@Repository
public interface ITechnologyRecordMapper extends Mapper<TechnologyRecord> {

    /**未退*/
    List<TechnologyRecord> allUse(@Param("houseFlowId")String houseFlowId);

    /**已验收*/
    List<TechnologyRecord> allChecked(@Param("houseFlowId")String houseFlowId);
}

