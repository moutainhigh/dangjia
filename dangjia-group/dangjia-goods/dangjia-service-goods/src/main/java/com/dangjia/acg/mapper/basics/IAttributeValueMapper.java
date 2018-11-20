
package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.attribute.AttributeValue;
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
public interface IAttributeValueMapper extends Mapper<AttributeValue> {
	void deleteById(String id);
	List<AttributeValue> query();
	//根据属性id查询所有属性选项
	List<AttributeValue> queryByAttributeId(String attribute_id);
	//根据属性id删除所有属性选项
	void deleteByAttributeId(String attribute_id);
}
