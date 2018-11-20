package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.brand.Brand;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**对数据库中Brand表操作的接口*/
@Repository
public interface IBrandMapper extends Mapper<Brand> {
	/**查询所有品牌*/
	List<Map<String,Object>> getBrand();
	/**查询所有品牌*/
	List<Brand> getBrands();
	/**根据拿到的name拿到品牌*/
	List<Brand> getBrandByNames(String name);
	Brand getBrandByName(String name);
	/**根据拿到的Id删除品牌*/
	void deleteById(String id);
}
