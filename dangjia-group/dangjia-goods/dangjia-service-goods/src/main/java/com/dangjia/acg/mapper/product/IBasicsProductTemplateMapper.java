package com.dangjia.acg.mapper.product;

import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.dto.basics.WorkerGoodsDTO;
import com.dangjia.acg.dto.product.DjBasicsProductTemplateDTO;
import com.dangjia.acg.dto.product.StorefrontProductDTO;
import com.dangjia.acg.modle.product.BasicsGoods;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @类 名： ProductDao
 * @功能描述： 商品dao
 * @作者信息： zmj
 * @创建时间： 2018-9-10下午2:28:37
 */
@Repository
public interface IBasicsProductTemplateMapper extends Mapper<DjBasicsProductTemplate> {

    /**
     * 按照 属性id 和属性值查找商品
     *
     * @param attributeIdArr
     * @param valueIdArr
     * @return
     */
    List<DjBasicsProductTemplate> getPListByValueIdArrOrAttrId(@Param("attributeIdArr") String attributeIdArr, @Param("valueIdArr") String valueIdArr);

    /**
     * 查询是否添加过同属性的商品
     * @param valueIdArr
     * @return
     */
    List<DjBasicsProductTemplate> getPListByValueIdArr(@Param("valueIdArr") String valueIdArr);

    /**
     * 根据名称查询商品
     * @param name
     * @return
     */
    List<DjBasicsProductTemplate> queryByName(@Param("name") String name,@Param("cityId") String cityId);

    /**
     * 根据编号查询商品
     * @param productSn
     * @return
     */
    List<DjBasicsProductTemplate> queryByProductSn(@Param("productSn") String productSn);

    /*更新单位*/
    void updateProductValueId(@Param("valueId") String valueId);

    List<DjBasicsProductTemplateDTO>  queryProductTemplateByGoodsId(@Param("categoryId") String  categoryId,@Param("storefontId") String  storefontId,@Param("bgtype") String bgtype,@Param("name") String name);
    //根据商品Id查货品
    List<DjBasicsProductTemplate> queryByGoodsId(@Param("goodsId") String goodsId);

    //根据商品Id查货品,随机截取12条
    List<DjBasicsProductTemplate> queryByGoodsIdLimit12(@Param("goodsId") String goodsId);

    //根据货品ID查询商品（暂存商品）
    DjBasicsProductTemplate queryTemporaryStorage(@Param("cityId") String cityId,@Param("goodsId") String goodsId,@Param("dataStatus") String dataStatus);

    /**
     * 删除材料扩展表信息
     * @param goodsId 货品ID
     */
    //void deleteProductMaterial(@Param("goodsId") String goodsId);

    /**
     * 删除产品扩展表信息
     */
   // void deleteProductWorker(@Param("goodsId") String goodsId);

    List<DjBasicsProductTemplate> queryProductByCategoryId(@Param("categoryId") String categoryId,@Param("cityId") String cityId);

    List<ActuarialProductAppDTO> serchCategoryProduct(@Param("categoryId") String categoryId,@Param("goodsId") String goodsId ,@Param("name")String[]   name, @Param("brandVal") String[] brandVal, @Param("attributeVal") String[] attributeVal, @Param("orderKey") String orderKey);

    List<DjBasicsProductTemplate> queryProductByTechnologyIds(@Param("technologyId") String technologyId);

    int updateProductCategoryByGoodsId(@Param("goodsId") String goodsId,@Param("categoryId")String categoryId);

    DjBasicsProductTemplate getById(String id);

    List<DjBasicsProductTemplate> queryProductData(@Param("name") String name, @Param("categoryId") String categoryId, @Param("productType") String productType,@Param("productId")String[] productId);


    List<DjBasicsProductTemplate> queryChooseGoods();
   // List<WorkerGoodsDTO> queryWorkerGoodsDTO(@Param("productSn") String productSn, @Param("workerTypeId") String workerTypeId);

    WorkerGoodsDTO queryStoreWorkerGoodsDTO(@Param("productSn") String productSn, @Param("workerTypeId") String workerTypeId);


    DjBasicsProductTemplate getProductListByStoreproductId(@Param("storefontProductId") String storefontProductId);

    //查询当前店铺下所有的货品
    List<BasicsGoods> getGoodsListByStorefontId(@Param("storefontId") String storefontId,
                                                @Param("categoryId") String categoryId);

    //查询当前店铺下对应货品下的所有商品
    List<DjBasicsProductTemplate> getProductTempListByStorefontId(@Param("storefontId") String storefontId,
                                                                  @Param("goodsId") String goodsId);

    //根据模板ID查询对应符合条件的商品信息
    StorefrontProductDTO getStorefrontInfoByprodTemplateId(@Param("prodTemplateId") String prodTemplateId, @Param("prodTemplateSn") String prodTemplateSn);



    /**
     * 查询商品库中的商品（但必须商品在店铺已上架，作配置商品用)
     * @param goodsId
     * @return
     */
    List<Map<String,Object>> getProductStoreListByGoodsId(@Param("goodsId") String goodsId);

    /**
     * 查询所有的人工商品
     * @param name
     * @return
     */
    List<Map<String,Object>> queryAllWorkerProductList(@Param("name") String name);

}
