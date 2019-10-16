package com.dangjia.acg.mapper.actuary;


import com.dangjia.acg.dto.actuary.SimulationTemplateConfigDetailDTO;
import com.dangjia.acg.modle.actuary.DjSimulationTemplateConfigDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created with IntelliJ IDEA.
 * Date: 2019/10/15
 * Time: 16:28
 */
@Repository
public interface DjSimulationTemplateConfigDetailMapper extends Mapper<DjSimulationTemplateConfigDetail> {

    /**
     * 查询设置编号
     * @param templateId
     * @return
     */
    String  selectCurrentIndexByTemplateId(@Param("templateId") String templateId);
    /**
     * 根据ID查询对应的详情信息
     * @return
     */
    SimulationTemplateConfigDetailDTO getSimulateionConfigDetail(@Param("simulationDetailId")String simulationDetailId);
}
