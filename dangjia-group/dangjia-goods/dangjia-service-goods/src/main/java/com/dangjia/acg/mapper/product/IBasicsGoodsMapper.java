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
	List<BasicsGoods> query(@Param("categoryId") String categoryId);
	BasicsGoods queryById(String id);
	List<BasicsGoods> queryByName(@Param("name") String name);

	//根据商品id查询关联品牌
	List<Brand> queryBrandByGid(@Param("goodsId") String goodsId);
	//根据商品id和品牌id查询关联品牌系列
	List<BrandSeries> queryBrandByGidAndBid(@Param("goodsId") String goodsId, @Param("brandId") String brandId);
	List<BasicsGoods> queryByCategoryId(@Param("categoryId") String categoryId);
	List<BasicsGoods> queryRepairGoods(@Param("name") String name, @Param("categoryId") String categoryId);
	List<BasicsGoods> queryGoodsList(@Param("categoryId") String categoryId, @Param("name") String name);

	//查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）
	List<BasicsGoods> queryGoodsListByCategoryLikeName(@Param("categoryId") String categoryId, @Param("name") String name);

	/**
	 * 查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）  // 去除：自购 包工包料 禁用 查询 product 为空
	 * @param categoryId
	 * @param name
	 * @param type /0:材料；1：包工包料
	 * @param buy 购买性质0：必买；1可选；2自购
	 * @return
	 */
	List<BasicsGoods> queryGoodsGroupListByCategoryLikeName(@Param("categoryId") String categoryId, @Param("name") String name, @Param("type") String type, @Param("buy") String buy);
}
