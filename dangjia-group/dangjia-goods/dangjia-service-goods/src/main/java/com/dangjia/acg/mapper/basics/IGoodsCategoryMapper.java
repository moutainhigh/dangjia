package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.attribute.GoodsCategory;
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
	List<GoodsCategory> queryCategoryByParentId(String parentId);
}
