package com.dangjia.acg.mapper.order;


import com.dangjia.acg.modle.product.CategoryLabel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IBillCategoryLabelMapper extends Mapper<CategoryLabel> {

}