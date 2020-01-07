package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.product.BasicsProductTemplateRatioDTO;
import com.dangjia.acg.modle.product.BasicsProductTemplateRatio;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface IProductTemplateRatioMapper extends Mapper<BasicsProductTemplateRatio> {

    /**
     * 查询当前商品下的责任比信息
     * @param productTemplateId
     * @return
     */
    List<BasicsProductTemplateRatioDTO> selectProductTemplateRatioList(@Param("productTemplateId") String productTemplateId);

}

