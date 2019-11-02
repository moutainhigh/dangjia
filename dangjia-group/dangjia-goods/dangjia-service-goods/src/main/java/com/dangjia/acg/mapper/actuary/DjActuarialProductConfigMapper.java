package com.dangjia.acg.mapper.actuary;


import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.actuary.app.SimulationCostCategoryDTO;
import com.dangjia.acg.modle.actuary.DjActuarialProductConfig;
import com.dangjia.acg.modle.actuary.DjActuarialTemplateConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: Date: 2019/10/15
 * Time: 16:28
 */
@Repository
public interface DjActuarialProductConfigMapper extends Mapper<DjActuarialProductConfig> {

    //List<DjActuarialProductConfig> queryActuarialProductByConfigId(@Param("actuarialTemplateId") String actuarialTemplateId);

    /**
     *  删除 无用的精算详情数据
     */
    void deleteActuarialProductByTemplate(@Param("cityId") String cityId);

    /**
     * 根据货品ID要询可切换的商品
     * @return
     */
    List<ActuarialProductAppDTO> searchChangeProductList(@Param("goodsId") String goodsId);

    /**
     * 查询分类汇总信息(模拟花费）
     */
    List<SimulationCostCategoryDTO> querySimulationCostByCategoryId(@Param("actuarialTemplateId") String actuarialTemplateId);

    /**
     * 查询模拟花费详情信息
     * @return
     */
    List<ActuarialProductAppDTO> querySimulationCostInfoList(@Param("actuarialTemplateId") String actuarialTemplateId,
                                                             @Param("categoryId") String categoryId);

}
