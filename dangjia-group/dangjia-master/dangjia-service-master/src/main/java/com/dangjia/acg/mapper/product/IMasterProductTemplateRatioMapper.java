package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.product.BasicsProductTemplateRatioDTO;
import com.dangjia.acg.modle.product.BasicsProductTemplateRatio;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Repository
public interface IMasterProductTemplateRatioMapper extends Mapper<BasicsProductTemplateRatio> {


}

