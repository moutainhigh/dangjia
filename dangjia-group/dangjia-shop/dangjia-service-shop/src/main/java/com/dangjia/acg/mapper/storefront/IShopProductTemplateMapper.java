package com.dangjia.acg.mapper.storefront;

import com.dangjia.acg.dto.product.DjBasicsProductTemplateDTO;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
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
public interface IShopProductTemplateMapper extends Mapper<DjBasicsProductTemplate> {



}
