package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.product.BasicsGoods;
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
public interface IGoodsMapper extends Mapper<BasicsGoods> {
	void deleteById(String id);
	List<BasicsGoods> query(@Param("categoryId") String categoryId);
	BasicsGoods queryById(String id);
	List<BasicsGoods> queryByName(@Param("name")String name);
	List<BasicsGoods> queryByCategoryId(@Param("categoryId")String categoryId);
	List<BasicsGoods> queryRepairGoods(@Param("name")String name,@Param("categoryId")String categoryId);
	List<BasicsGoods> queryGoodsList(@Param("categoryId")String categoryId,@Param("name")String name);

	//查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）
	List<BasicsGoods> queryGoodsListByCategoryLikeName(@Param("categoryId")String categoryId,
												 @Param("name")String name,
												 @Param("cityId")String cityId);

	/**
	 * 查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）  // 去除：自购 包工包料 禁用 查询 product 为空
	 * @param categoryId
	 * @param name
	 * @param type /0:材料；1：包工包料
	 * @param buy 购买性质0：必买；1可选；2自购
	 * @return
	 */
	List<BasicsGoods> queryGoodsGroupListByCategoryLikeName(@Param("categoryId")String categoryId,@Param("name")String name,@Param("type")String type,@Param("buy")String buy,@Param("cityId")String cityId);
}
