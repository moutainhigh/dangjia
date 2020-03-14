package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.product.CategoryGoodsProductDTO;
import com.dangjia.acg.modle.basics.HomeProductDTO;
import com.dangjia.acg.modle.brand.Brand;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 
   * @类 名： BasicsGoodsCategoryDao
   * @功能描述： 类别dao
   * @作者信息： fzh
   * @创建时间： 2019-9-11
 */
@Repository
public interface IBasicsGoodsCategoryMapper extends Mapper<BasicsGoodsCategory> {
	void deleteById(String id);

	//根据父id查询下属商品类型
	List<BasicsGoodsCategory> queryCategoryByParentId(@Param("cityId") String cityId,@Param("parentId") String parentId,@Param("categoryLabelId") String categoryLabelId,@Param("ownerDisplay") String ownerDisplay);

	//查询所有的末级分类的类别
	List<BasicsGoodsCategory>  queryLastCategoryList(@Param("cityId") String cityId,@Param("searchKey") String  searchKey);

	//根据name查询商品对象
	List<BasicsGoodsCategory> queryCategoryByName(@Param("name") String name,@Param("cityId") String cityId);

	//根据name查询商品对象
	List<HomeProductDTO> getProductList(@Param("categoryId") String categoryId);

	//删除类别关联品牌系列
	void deleteCategorysSeries(@Param("categoryId")String categoryId);
    //根据商品id查询关联品牌
    List<Brand> queryBrandByCategoryid(@Param("categoryId")String categoryId,
									   @Param("cityId")String cityId);
    //根据商分类顶级id查询关联品牌
    List<Brand> queryBrandByTopCategoryid(@Param("categoryId")String categoryId,@Param("wordKey")String wordKey);

    List<BasicsGoodsCategory> getAllCategoryChildById(@Param("parentTop")String parentTop);

	//根据父id查询下属商品类型
	List<BasicsGoodsCategory> queryGoodsCategoryExistlastCategory(@Param("parentId") String parentId,
																  @Param("cityId") String cityId);

    List<CategoryGoodsProductDTO> queryCategoryListByCategoryLikeName(@Param("categoryId") String categoryId,
																      @Param("goodsName") String goodsName,
																	  @Param("cityId") String cityId);

	/**
	 * 查询符合条件的维保商品的顶级分类
	 * @param cityId 城市ID
	 * @param workerTypeId 工种ID
	 * @return
	 */
	List<BasicsGoodsCategory> queryMaintenanceRecordTopCategory(@Param("cityId") String cityId,
																@Param("workerTypeId") String workerTypeId);
}
