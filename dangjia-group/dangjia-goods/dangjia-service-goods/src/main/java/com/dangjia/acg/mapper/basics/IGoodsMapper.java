package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.brand.GoodsSeries;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 
 * @ClassName: GoodsDao
 * @Description: 商品dao
 * @author: zmj
 * @date: 2018-9-18上午9:49:19
 */
@Repository
public interface IGoodsMapper extends Mapper<Goods> {
	void deleteById(String id);
	List<Goods> query(@Param("categoryId") String categoryId);
	Goods queryById(String id);
	List<Goods> queryByName(@Param("name")String name);
	//新增商品关联品牌系列
	void insertGoodsSeries(GoodsSeries goodsSeries);
	//删除商品关联品牌系列
	void deleteGoodsSeries(@Param("goodsId")String goodsId);
	//根据商品id查询关联品牌
	List<Brand> queryBrandByGid(@Param("goodsId")String goodsId);
	//根据商品id和品牌id查询关联品牌系列 
	List<BrandSeries> queryBrandByGidAndBid(@Param("goodsId") String goodsId, @Param("brandId") String brandId);
	List<Goods> queryByCategoryId(@Param("categoryId")String categoryId);
	List<Goods> queryRepairGoods(@Param("name")String name,@Param("categoryId")String categoryId);
	List<Goods> queryGoodsList(@Param("categoryId")String categoryId,@Param("name")String name);

	//查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）
	List<Goods> queryGoodsListByCategoryLikeName(@Param("categoryId")String categoryId,@Param("name")String name);
}
