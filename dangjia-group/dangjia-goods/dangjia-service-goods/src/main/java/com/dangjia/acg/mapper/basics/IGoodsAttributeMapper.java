package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.attribute.CategoryAttribute;
import com.dangjia.acg.modle.attribute.GoodsAttribute;
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
public interface IGoodsAttributeMapper extends Mapper<GoodsAttribute> {
	void deleteById(String id);
	List<GoodsAttribute> query();
	//根据id查询属性对象
	GoodsAttribute queryById(String id);
	//根据类别id查询关联属性
	List<GoodsAttribute> queryCategoryAttribute(String categoryId);
	//根据属性名称模糊查询属性
	List<GoodsAttribute> queryGoodsAttributelikeName(String name);
	//新增类别和属性关联
	void insertCategoryAttribute(CategoryAttribute categoryAttribute);
	//删除商品类别和属性关联 
	void deleteCategoryAttribute(String attribute_id);
}
