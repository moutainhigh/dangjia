package com.dangjia.acg.mapper.actuary;


import com.dangjia.acg.dto.actuary.SimulationTemplateConfigDTO;
import com.dangjia.acg.dto.actuary.app.SimulationTemplateAppConfigDTO;
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

    List<SimulationTemplateConfigDTO> querySimulateionTemplateConfig(@Param("id") String id,@Param("addressUrl") String addressUrl,@Param("cityId") String cityId);

    String  selectCurrentIndexByConfigType(@Param("configType") String configType);
    //删除符合条件的标题数据
    void deleteSimulationTemplate(@Param("cityId") String cityId);

    /**
     * 查询组合返回列表
     * @return
     */
    List queryTemplateListByType(@Param("cityId") String cityId);

    /**
     * 我要装修--模拟花费标题查询
     * @return
     */
    List<SimulationTemplateAppConfigDTO> searchSimulationTitleList(@Param("cityId") String cityId);

}
