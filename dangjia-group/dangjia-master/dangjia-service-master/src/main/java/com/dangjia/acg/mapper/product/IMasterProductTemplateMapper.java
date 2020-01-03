package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.product.DjBasicsProductTemplateDTO;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 26/11/2019
 * Time: 上午 10:32
 */
@Repository
public interface IMasterProductTemplateMapper extends Mapper<DjBasicsProductTemplate> {

    List<DjBasicsProductTemplate> querySkillsCertificationWaitingList(@Param("workerTypeId") Integer workerTypeId,
                                                                      @Param("skillCertificationId") String skillCertificationId,
                                                                      @Param("searchKey") String searchKey,
                                                                      @Param("cityId") String cityId);

    List<DjBasicsProductTemplateDTO> queryProductData(@Param("houseId") String houseId,
                                                      @Param("name") String name,
                                                      @Param("categoryId") String categoryId,
                                                      @Param("productType") String productType);

}
