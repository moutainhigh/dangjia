package com.dangjia.acg.mapper.actuary;


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

    List<DjActuarialProductConfig> queryActuarialProductByConfigId(@Param("actuarialTemplateId") String actuarialTemplateId);
}
