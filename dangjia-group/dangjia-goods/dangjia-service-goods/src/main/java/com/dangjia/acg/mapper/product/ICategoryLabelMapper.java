package com.dangjia.acg.mapper.product;


import com.dangjia.acg.modle.product.CategoryLabel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface ICategoryLabelMapper extends Mapper<CategoryLabel> {

    /**c查找所有的类别标签*/
    List<CategoryLabel> getCategoryLabel(String cityId);
    /**根据拿到的name拿到标签对象*/
    List<CategoryLabel> getCategoryLabelByName(@Param("name")String name,@Param("cityId") String cityId);

    /**查询绑定过的标签*/
    List<CategoryLabel> queryAPPCategoryLabel();
     //获取当前有多少条标签
    int getCategoryCountLabel(@Param("cityId") String cityId);
}