package com.dangjia.acg.mapper.product;


import com.dangjia.acg.modle.product.CategoryLabel;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ICategoryLabelMapper extends Mapper<CategoryLabel> {

    /**c查找所有的类别标签*/
    List<CategoryLabel> getCategoryLabel();
    /**根据拿到的name拿到标签对象*/
    List<CategoryLabel> getCategoryLabelByName(String name);

    /**查询绑定过的标签*/
    List<CategoryLabel> queryAPPCategoryLabel();
}