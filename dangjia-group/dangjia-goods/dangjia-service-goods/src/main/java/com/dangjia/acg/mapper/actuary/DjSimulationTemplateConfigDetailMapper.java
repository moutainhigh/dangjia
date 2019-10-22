package com.dangjia.acg.mapper.actuary;


import com.dangjia.acg.dto.actuary.SimulationTemplateConfigDetailDTO;
import com.dangjia.acg.dto.actuary.app.SimulationTemplateConfigDetailAppDTO;
import com.dangjia.acg.modle.actuary.DjSimulationTemplateConfigDetail;
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

    /**
     * 修改临时数据的状态为正式数据
     */
    void updateTemplateDetailConfig();

    /**
     * 添加一批新的临时数据(和正式数据保持一致）
     */
    void batchInsertTempateDetail();

    /**
     *  我要装修，根据标题ID，查询标题 详情列表信息
     * @param simulationTemplateId 标题 ID
     * @param addressUrl
     * @return
     */
    List<SimulationTemplateConfigDetailAppDTO> searchSimulationTitleDetailList(@Param("simulationTemplateId")String simulationTemplateId, @Param("addressUrl") String addressUrl);
}
