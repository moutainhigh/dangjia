
package com.dangjia.acg.mapper.basics;

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
public interface IAttributeValueMapper extends Mapper<AttributeValue> {
	void deleteById(String id);
	//根据id查询属性选项对象
	AttributeValue queryById(String id);

	//根据id查询属性选项PO对象
	AttributeValuePO getPOById(String id);

	//查询所有属性选项
	List<AttributeValue> query();

	//根据属性id查询所有属性选项PO对象
	List<AttributeValuePO> queryPOByAttributeId(@Param("attributeId")String attributeId);

	//根据属性id查询所有属性选项
	List<AttributeValue> queryByAttributeId(@Param("attributeId")String attributeId);
	//根据属性id删除所有属性选项
	void deleteByAttributeId(@Param("attributeId")String attributeId);
}
