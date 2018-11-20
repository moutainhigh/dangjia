package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.attribute.AttributeValue;
import com.dangjia.acg.modle.basics.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * 
   * @类 名： ProductDao
   * @功能描述： 商品dao
   * @作者信息： zmj
   * @创建时间： 2018-9-10下午2:28:37
 */
@Repository
public interface IProductMapper extends Mapper<Product> {
	void deleteById(String id);
	Product getById(String id);
	List<Product> query(@Param("category_id") String category_id);
	//根据货品id查询所有关联属性
	List<AttributeValue> queryProductAttributeByPid(String product_id);
	//根据商品Id查货品
	List<Product> queryByGoodsId(String goods_id);
	List<Product> queryByName(@Param("name") String name);
}
