package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.brand.BrandSeries;
import com.dangjia.acg.modle.basics.Goods;
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
	//新增商品关联品牌系列
	void insertGoodsSeries(GoodsSeries goodsSeries);
	//删除商品关联品牌系列
	void deleteGoodsSeries(@Param("goodsId")String goodsId);
	//根据商品id查询关联品牌
	List<Brand> queryBrandByGid(@Param("goodsId")String goodsId);
	//根据商品id和品牌id查询关联品牌系列 
	List<BrandSeries> queryBrandByGidAndBid(@Param("goodsId") String goodsId, @Param("brandId") String brandId);
	List<Goods> queryByCategoryId(@Param("categoryId")String categoryId);
}
