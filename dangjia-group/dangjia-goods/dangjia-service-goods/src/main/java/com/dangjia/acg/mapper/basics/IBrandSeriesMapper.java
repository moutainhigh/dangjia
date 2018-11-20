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
	/**查询所有品牌*/
	List<BrandSeries> queryBrandSeries(@Param("brand_id") String brand_id);
	/**根据拿到的Id删除品牌*/
	void deleteById(String brandExplainId);
}
