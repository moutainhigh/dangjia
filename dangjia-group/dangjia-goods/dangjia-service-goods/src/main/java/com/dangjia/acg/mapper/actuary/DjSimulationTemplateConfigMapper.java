package com.dangjia.acg.mapper.actuary;


import com.dangjia.acg.dto.actuary.SimulationTemplateConfigDTO;
import com.dangjia.acg.modle.actuary.DjSimulationTemplateConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/10/15
 * Time: 16:28
 */
@Repository
public interface DjSimulationTemplateConfigMapper extends Mapper<DjSimulationTemplateConfig> {

    List<SimulationTemplateConfigDTO> querySimulateionTemplateConfig(@Param("id") String id);

    String  selectCurrentIndexByConfigType(@Param("configType") String configType);
}
