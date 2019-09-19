package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.product.DjBasicsAttributeValue;
import com.dangjia.acg.pojo.product.DjBasicsAttributeValuePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/15
 * Time: 14:50
 */
@Repository
public interface DjBasicsAttributeValueMapper extends Mapper<DjBasicsAttributeValue> {


    //根据属性id查询所有属性选项PO对象
    List<DjBasicsAttributeValuePO> queryPOByAttributeId(@Param("attributeId")String attributeId);
}
