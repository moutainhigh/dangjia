
package com.dangjia.acg.mapper.core;

import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.pojo.attribute.AttributeValuePO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 
   * @类 名： AttributeValueDao
   * @功能描述： 属性值dao
   * @作者信息： zmj
   * @创建时间： 2018-9-10下午2:28:37
 */
@Repository
public interface IMasterAttributeValueMapper extends Mapper<AttributeValue> {

}
