package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.basics.HomeProductDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 
   * @类 名： GoodsCategoryDao
   * @功能描述： 类别dao
   * @作者信息： zmj
   * @创建时间： 2018-9-10下午2:28:37
 */
@Repository
public interface IGoodsCategoryMapper extends Mapper<GoodsCategory> {
	void deleteById(String id);
	List<GoodsCategory> query();

	//根据父id查询下属商品类型
	List<GoodsCategory> queryCategoryByParentId(@Param("parentId") String parentId,@Param("cityId") String cityId);

	//根据name查询商品对象
	List<GoodsCategory> queryCategoryByName(@Param("name") String name,@Param("cityId") String cityId);

	//根据name查询商品对象
	List<HomeProductDTO> getProductList(@Param("categoryId") String categoryId,@Param("cityId") String cityId);
}
