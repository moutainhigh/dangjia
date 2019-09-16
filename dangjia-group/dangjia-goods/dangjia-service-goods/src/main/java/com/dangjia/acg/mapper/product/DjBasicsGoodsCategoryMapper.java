package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.DjBasicsGoodsCategory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/15
 * Time: 10:03
 */
@Repository
public interface DjBasicsGoodsCategoryMapper extends Mapper<DjBasicsGoodsCategory> {

    //根据父id查询下属商品类型
    List<DjBasicsGoodsCategory> queryCategoryByParentId(@Param("parentId") String parentId);

}
