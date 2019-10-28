package com.dangjia.acg.mapper.actuary;


import com.dangjia.acg.dto.actuary.ActuarialTemplateConfigDTO;
import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.actuary.app.ActuarialTemplateConfigAppDTO;
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
public interface DjActuarialTemplateConfigMapper extends Mapper<DjActuarialTemplateConfig> {

    List<ActuarialTemplateConfigDTO> queryActuarialTemplateConfig(@Param("id") String id,@Param("cityId")  String cityId);

    /**
     * app端查询设计精算显示列表及对应的货品
     * @return
     */
    List<ActuarialTemplateConfigAppDTO> searchAppActuarialList();


}
