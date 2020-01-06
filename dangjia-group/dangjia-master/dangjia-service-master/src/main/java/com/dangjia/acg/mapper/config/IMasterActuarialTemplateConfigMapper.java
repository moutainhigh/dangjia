package com.dangjia.acg.mapper.config;


import com.dangjia.acg.dto.actuary.ActuarialTemplateConfigDTO;
import com.dangjia.acg.dto.actuary.app.ActuarialTemplateConfigAppDTO;
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
public interface IMasterActuarialTemplateConfigMapper extends Mapper<DjActuarialTemplateConfig> {



}
