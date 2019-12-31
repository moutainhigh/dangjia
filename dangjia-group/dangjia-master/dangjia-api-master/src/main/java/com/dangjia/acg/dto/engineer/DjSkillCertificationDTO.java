package com.dangjia.acg.dto.engineer;

import com.dangjia.acg.modle.engineer.DjSkillCertification;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 30/12/2019
 * Time: 下午 2:17
 */
@Data
public class DjSkillCertificationDTO {

    private List<DjSkillCertification> djSkillCertifications;

    private List<DjBasicsProductTemplate> djBasicsProductTemplates;
}
