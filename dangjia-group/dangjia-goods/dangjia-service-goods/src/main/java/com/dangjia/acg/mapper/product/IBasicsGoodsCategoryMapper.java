package com.dangjia.acg.mapper.product;

import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.basics.HomeProductDTO;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 
   * @类 名： BasicsGoodsCategoryDao
   * @功能描述： 类别dao
   * @作者信息： fzh
   * @创建时间： 2019-9-11
 */
@Repository
public interface IBasicsGoodsCategoryMapper extends Mapper<BasicsGoodsCategory> {
	void deleteById(String id);
	List<BasicsGoodsCategory> query();

	//根据父id查询下属商品类型
	List<BasicsGoodsCategory> queryCategoryByParentId(@Param("parentId") String parentId);

	//根据name查询商品对象
	List<BasicsGoodsCategory> queryCategoryByName(@Param("name") String name);

	//根据name查询商品对象
	List<HomeProductDTO> getProductList(@Param("categoryId") String categoryId);
}
