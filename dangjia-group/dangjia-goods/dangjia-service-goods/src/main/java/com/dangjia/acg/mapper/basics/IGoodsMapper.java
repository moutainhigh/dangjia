package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.basics.Goods;
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
	List<Goods> queryByCategoryId(@Param("categoryId")String categoryId);
	List<Goods> queryRepairGoods(@Param("name")String name,@Param("categoryId")String categoryId);
	List<Goods> queryGoodsList(@Param("categoryId")String categoryId,@Param("name")String name);

	//查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）
	List<Goods> queryGoodsListByCategoryLikeName(@Param("categoryId")String categoryId,@Param("name")String name);

	/**
	 * 查询某个分类的商品 模糊name（如果categoryId 为null，查询全部材料商品 ）  // 去除：自购 包工包料 禁用 查询 product 为空
	 * @param categoryId
	 * @param name
	 * @param type /0:材料；1：包工包料
	 * @param buy 购买性质0：必买；1可选；2自购
	 * @return
	 */
	List<Goods> queryGoodsGroupListByCategoryLikeName(@Param("categoryId")String categoryId,@Param("name")String name,@Param("type")String type,@Param("buy")String buy);
}
