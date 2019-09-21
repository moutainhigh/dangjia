package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.product.DjBasicsActuarialConfigurationDTO;
import com.dangjia.acg.modle.product.DjBasicsActuarialConfiguration;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/20
 * Time: 16:28
 */
@Repository
public interface DjBasicsActuarialConfigurationMapper extends Mapper<DjBasicsActuarialConfiguration> {

    List<DjBasicsActuarialConfigurationDTO> queryConfiguration();

    List<DjBasicsActuarialConfiguration> querySingleConfiguration(@Param("phaseId") String phaseId);
}
