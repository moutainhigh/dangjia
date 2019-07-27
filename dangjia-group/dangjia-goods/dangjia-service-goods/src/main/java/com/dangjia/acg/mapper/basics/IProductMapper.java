package com.dangjia.acg.mapper.basics;

import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.pojo.basics.ProductPO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @类 名： ProductDao
 * @功能描述： 商品dao
 * @作者信息： zmj
 * @创建时间： 2018-9-10下午2:28:37
 */
@Repository
public interface IProductMapper extends Mapper<Product> {

    void deleteById(String id);

    Product getById(String id);

    List<Product> query(@Param("category_id") String category_id);

    //根据商品Id查货品
    List<Product> queryByGoodsId(@Param("goodsId") String goodsId);

    //根据商品Id 和 品牌系列id查货品
    List<Product> queryByGoodsIdAndbrandSeriesId(@Param("goodsId") String goodsId, @Param("brandSeriesId") String brandSeriesId);

    //根据商品Id 和 品牌系列id查货品
    List<Product> queryByGoodsIdAndbrandSeriesIdAndBrandId(@Param("goodsId") String goodsId, @Param("brandId") String brandId, @Param("brandSeriesId") String brandSeriesId);

    List<Product> queryByName(@Param("name") String name);

    List<Product> queryByProductSn(@Param("productSn") String productSn);

    List<Product> queryByLikeName(@Param("name") String name);

    //根据标签Id查货品
    List<Product> queryByLabelId(@Param("labelId") String labelId);

    //根据标签Id查货品
    List<ProductPO> queryPOByLabelId(@Param("labelId") String labelId);

    /**
     * 查询product
     */
    Product selectProduct(@Param("goodsId") String goodsId, @Param("brandId") String brandId, @Param("brandSeriesId") String brandSeriesId, @Param("valueIdArr") String[] valueIdArr);
//    Product selectProduct(@Param("goodsId") String goodsId, @Param("brandId") String brandId, @Param("brandSeriesId") String brandSeriesId, @Param("valueIdArr") String valueIdArr);

    List<Product> getPListByBrandSeriesId(@Param("brandId") String brandId, @Param("brandSeriesId") String brandSeriesId, @Param("valueIdArr") String valueIdArr);

    List<Product> getPListByValueIdArrByNullBrandId(@Param("valueIdArr") String valueIdArr);

    /**
     * 按照 属性id 和属性值查找商品
     *
     * @param attributeIdArr
     * @param valueIdArr
     * @return
     */
    List<Product> getPListByValueIdArrOrAttrId(@Param("attributeIdArr") String attributeIdArr, @Param("valueIdArr") String valueIdArr);

    //查询 无品牌无系列有属性值的商品
    List<Product> getPListByGoodsIdAndNullBrandId(@Param("goodsId") String goodsId);

    List<Product> getPListByBrandSeriesIdAndNullValueId(@Param("brandId") String brandId, @Param("brandSeriesId") String brandSeriesId);

    List<Product> queryRepairBudgetMaterial(@Param("houseId") String houseId, @Param("name") String name, @Param("categoryId") String categoryId);

    List<Product> queryProductData(@Param("name") String name, @Param("categoryId") String categoryId, @Param("productType") String productType,@Param("productId")String[] productId);

    Product getSwitchProduct(@Param("brandSeriesId") String brandSeriesId, @Param("valueIdArr") String[] valueIdArr);

    /**
     * 模糊搜索商品
     *
     * @param name 用户给定的商品名称，可以是product / basics_brand / basics_brand_series 中的name的一种
     * @return 到的goods中的商品名称
     */
    List<Product> serchBoxName(@Param("name") String name);

    Product selectProductByGoodsIdAndBrandIdAndBrandSeriesId(String goodsId, String brandId, String brandSeriesId);

    /*更新商品名称*/
    void updateProductById(@Param("oldName") String oldName,@Param("newName") String newName,@Param("brandSeriesId") String brandSeriesId,@Param("brandId") String brandId,
                           @Param("goodsId") String goodsId,@Param("id") String id);

    /*更新单位*/
    void updateProductByUnitId(@Param("unitName") String unitName,@Param("unitId") String unitId);


    /*更新单位*/
    void updateProductValueId(@Param("valueId") String valueId);
}
