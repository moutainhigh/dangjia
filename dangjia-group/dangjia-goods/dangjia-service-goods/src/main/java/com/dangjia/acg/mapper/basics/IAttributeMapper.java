package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.attribute.Attribute;
import com.dangjia.acg.pojo.attribute.AttributePO;
import com.dangjia.acg.modle.attribute.CategoryAttribute;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 
   * @类 名： GoodsAttributeDao
   * @功能描述：属性类别dao
   * @作者信息： zmj
   * @创建时间： 2018-9-10下午2:28:37
 */
@Repository
public interface IAttributeMapper extends Mapper<Attribute> {

	/**根据类别id查询关联价格属性*/
	List<Attribute> queryPriceAttribute(String categoryId);
	void deleteById(String id);
	List<Attribute> query();
	//根据id查询属性对象
	Attribute queryById(String id);

	//根据id查询属性对象
	AttributePO queryPOById(String id);

	/* 弃用：//根据类别id查询关联属性  用 queryAttributeByCategoryId 方法代替 */
	List<Attribute> queryCategoryAttribute(String categoryId); //弃用
	//根据类别id查询关联属性
	List<Attribute> queryAttributeByCategoryId(@Param("categoryId") String categoryId);

	//根据属性名称模糊查询属性
	List<Attribute> queryGoodsAttributelikeName(String name);

	//查询所有的类别和属性关联表数据
	List<CategoryAttribute>  selectAllCategoryAttribute();
//	//新增类别和属性关联
//	void insertCategoryAttribute(CategoryAttribute categoryAttribute);//弃用
//	//删除商品类别和属性关联
//	void deleteCategoryAttribute(String attribute_id);//弃用
}
