package com.dangjia.acg.mapper.product;


import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.product.BasicsGoods;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 *
 * @ClassName: BasicsGoodsDao
 * @Description: 商品dao
 * @author: fzh
 * @date: 2019-9-11
 */

@Repository
public interface IBasicsGoodsMapper extends Mapper<BasicsGoods> {
	void deleteById(String id);
	List<BasicsGoods> query(@Param("categoryId") String categoryId,@Param("cityId") String cityId);
	BasicsGoods queryById(String id);
	List<BasicsGoods> queryByName(@Param("name") String name,@Param("cityId") String cityId);
	List<BasicsGoods> queryByCategoryId(@Param("categoryId") String categoryId,@Param("cityId") String cityId);

	//查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）
	List<BasicsGoods> queryGoodsListByCategoryLikeName(@Param("categoryId") String categoryId, @Param("name") String name, @Param("cityId") String cityId);
	 //根据类别查询所有店铺售卖的货品
	List<BasicsGoods> getActuarialGoodsListByCategoryId(@Param("categoryId") String categoryId, @Param("cityId") String cityId);
}
