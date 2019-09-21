package com.dangjia.acg.dto.product;

import com.dangjia.acg.modle.product.DjBasicsActuarialConfiguration;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/21
 * Time: 14:04
 */
@Data
public class SingleConfigurationDTO {
    private String phaseId;
    private List<DjBasicsActuarialConfiguration> djBasicsActuarialConfiguration;
}
