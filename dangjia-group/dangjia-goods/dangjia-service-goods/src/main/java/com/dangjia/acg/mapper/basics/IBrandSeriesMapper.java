package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.brand.BrandSeries;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 品牌系列
 * @ClassName: IBrandSeriesMapper
 * @Description: TODO
 * @author: zmj
 * @date: 2018-9-20下午4:57:44
 */
@Repository
public interface IBrandSeriesMapper extends Mapper<BrandSeries> {

	/**根据货品id查询品牌系列名*/
	String brandSeriesName(@Param("productId")String productId);
	/**根据货品id查询品牌系列名*/
	BrandSeries brandSeriesByPid(@Param("productId")String productId);
	/**查询所有品牌*/
	List<BrandSeries> queryBrandSeries(@Param("brandId") String brandId);
	/**根据拿到的Id删除品牌*/
	void deleteById(String brandExplainId);
	/**根据商品id查询关联品牌系列*/
	List<BrandSeries> queryBrandByGid(@Param("goodsId") String goodsId);
}
