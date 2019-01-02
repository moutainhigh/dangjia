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

    /**已验收*/
    List<TechnologyRecord> allChecked(@Param("houseId")String houseId,@Param("workerTypeId")String workerTypeId);

    /**查询节点*/
    List<TechnologyRecord> checkByTechnologyId(@Param("houseId")String houseId,@Param("technologyId")String technologyId);
}

