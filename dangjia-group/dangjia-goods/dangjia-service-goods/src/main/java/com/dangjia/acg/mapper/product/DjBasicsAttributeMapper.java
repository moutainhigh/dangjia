package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.actuary.AttributeDTO;
import com.dangjia.acg.modle.product.DjBasicsAttribute;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/15
 * Time: 14:37
 */
@Repository
public interface DjBasicsAttributeMapper extends Mapper<DjBasicsAttribute> {

    //根据类别id查询关联属性
    List<DjBasicsAttribute> queryAttributeByCategoryId(@Param("categoryId") String categoryId, @Param("likeAttrName") String likeAttrName);

    List<DjBasicsAttribute> queryAttributeByCategoryIdAndAttrName(@Param("categoryId") String categoryId, @Param("attrName") String attrName);

    List<AttributeDTO> queryAttributeDatas(@Param("categoryId") String categoryId);


}
