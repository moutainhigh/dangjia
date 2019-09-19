package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.product.AppBasicsProductDTO;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.product.DjBasicsProduct;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 产品dao
 * author: LJL
 * Date: 2019/7/25
 * Time: 13:56
 */
@Repository
public interface DjBasicsProductMapper extends Mapper<DjBasicsProduct> {

    /**
     * 按照 属性id 和属性值查找商品
     *
     * @param attributeIdArr
     * @param valueIdArr
     * @return
     */
    List<DjBasicsProduct> getPListByValueIdArrOrAttrId(@Param("attributeIdArr") String attributeIdArr, @Param("valueIdArr") String valueIdArr);

    /**
     * 查询是否添加过同属性的商品
     * @param valueIdArr
     * @return
     */
    List<DjBasicsProduct> getPListByValueIdArr(@Param("valueIdArr") String valueIdArr);

    /**
     * 根据名称查询商品
     * @param name
     * @return
     */
    List<DjBasicsProduct> queryByName(@Param("name") String name);

    /**
     * 根据编号查询商品
     * @param productSn
     * @return
     */
    List<DjBasicsProduct> queryByProductSn(@Param("productSn") String productSn);

    List<AppBasicsProductDTO> queryProductMaterial(@Param("productSn") String productSn);

    List<AppBasicsProductDTO> queryProductWorker(@Param("productSn") String productSn);

    /*更新单位*/
    void updateProductValueId(@Param("valueId") String valueId);


    //根据商品Id查货品
    List<DjBasicsProduct> queryByGoodsId(@Param("goodsId") String goodsId);

    //根据货品ID查询商品（暂存商品）
    DjBasicsProduct queryTemporaryStorage(@Param("goodsId") String goodsId,@Param("dataStatus") String dataStatus);

    /**
     * 删除材料扩展表信息
     * @param goodsId 货品ID
     */
    void deleteProductMaterial(@Param("goodsId") String goodsId);

    /**
     * 删除产品扩展表信息
     * @param goodsId 货品ID
     */
    void deleteProductWorker(@Param("goodsId") String goodsId);

    List<DjBasicsProduct> queryProductByCategoryId(@Param("categoryId") String categoryId);

    List<DjBasicsProduct> serchCategoryProduct(@Param("categoryId") String categoryId, @Param("name") String name, @Param("brandVal") String brandVal, @Param("attributeVal") String[] attributeVal, @Param("orderKey") String orderKey);

    List<DjBasicsProduct> queryProductByTechnologyIds(@Param("technologyId") String technologyId);

    int updateProductCategoryByGoodsId(@Param("goodsId") String goodsId,@Param("categoryId")String categoryId);

    DjBasicsProduct getById(String id);

}
