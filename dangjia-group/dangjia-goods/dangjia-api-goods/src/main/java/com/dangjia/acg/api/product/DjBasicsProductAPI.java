package com.dangjia.acg.api.product;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
import com.dangjia.acg.dto.product.BasicsProductDTO;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.product.ProductAddedRelation;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2019/9/11
 * Time: 13:56
 */
@Api(description = "商品3.0管理接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsProductAPI {


    @PostMapping("/app/product/djBasicsProduct/queryProductData")
    @ApiOperation(value = "app根据商品名称查询商品信息", notes = "app根据商品名称查询商品信息")
    ServerResponse queryProductData(@RequestParam("request")HttpServletRequest request,
                                    @RequestParam("name")String name,
                                    @RequestParam("cityId")String cityId);


    @PostMapping("/app/product/djBasicsProduct/getNewValueNameArr")
    @ApiOperation(value = "根据货品id查询规格", notes = "根据货品id查询规格")
    String getNewValueNameArr(@RequestParam("valueIdArr")String valueIdArr);


    @PostMapping("/product/djBasicsProduct/queryProductLabels")
    @ApiOperation(value = "查询商品打的标签", notes = "查询商品打的标签")
    ServerResponse queryProductLabels(@RequestParam("request")HttpServletRequest request,
                                      @RequestParam("productId")String productId,
                                      @RequestParam("cityId")String cityId);

    @PostMapping("/product/djBasicsProduct/addLabelsValue")
    @ApiOperation(value = "商品打标签", notes = "商品打标签")
    ServerResponse addLabelsValue(@RequestParam("request")HttpServletRequest request,
                                  @RequestParam("jsonStr")String jsonStr,
                                  @RequestParam("cityId")String cityId);

    @PostMapping("app/product/queryDataByProductId")
    @ApiOperation(value = "根据货品编号查看商品详情", notes = "根据货品编号查看商品详情")
    DjBasicsProductTemplate queryDataByProductId(@RequestParam("productId")String productId);


    @PostMapping("/product/djBasicsProduct/saveBasicsGoods")
    @ApiOperation(value = "新增货品", notes = "新增货品")
    ServerResponse saveBasicsGoods(@RequestParam("request") HttpServletRequest request,
                                   BasicsGoodsDTO basicsGoodsDTO);

    @PostMapping("/product/djBasicsProduct/updateBasicsGoods")
    @ApiOperation(value = "修改货品", notes = "修改货品")
    ServerResponse updateBasicsGoods(@RequestParam("request") HttpServletRequest request,
                               BasicsGoodsDTO basicsGoodsDTO);

    @PostMapping("/product/djBasicsProduct/insertBatchProduct")
    @ApiOperation(value = "批量新增修改货品下的商品", notes = "批量新增修改货品下的商品")
    ServerResponse insertBatchProduct(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("productArr") String productArr,
                                      @RequestParam("cityId")String cityId,
                                      @RequestParam("user_id")String user_id);

    @PostMapping("/product/djBasicsProduct/queryGoodsListByCategoryLikeName")
    @ApiOperation(value = "按照name模糊查询商品及下属货品", notes = "按照name模糊查询商品及下属货品，type： 是否禁用  0：禁用；1不禁用 ;  -1全部默认")
    ServerResponse queryGoodsListByCategoryLikeName(@RequestParam("request") HttpServletRequest request,
                                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                                    @RequestParam("categoryId") String categoryId,
                                                    @RequestParam("name") String name,
                                                    @RequestParam("cityId") String cityId,
                                                    @RequestParam("type") Integer type,
                                                    @RequestParam("categoryName") String categoryName);

    @PostMapping("/product/djBasicsProduct/saveProductTemporaryStorage")
    @ApiOperation(value = "暂存商品信息", notes = "暂存商品信息")
    ServerResponse saveProductTemporaryStorage(@RequestParam("request") HttpServletRequest request,
                                 BasicsProductDTO basicsProductDTO,
                                 @RequestParam("technologyList") String technologyList,
                                 @RequestParam("deleteTechnologyIds") String  deleteTechnologyIds,
                                 @RequestParam("cityId") String  cityId,
                                 @RequestParam("user_id")String user_id);

    @PostMapping("/product/djBasicsProduct/editSingleProduct")
    @ApiOperation(value = "单个新增修改货品下的商品", notes = "单个新增修改货品下的商品")
    ServerResponse editSingleProduct(@RequestParam("request") HttpServletRequest request,
                                 BasicsProductDTO basicsProductDTO,
                                 @RequestParam("technologyList") String technologyList,
                                 @RequestParam("deleteTechnologyIds") String  deleteTechnologyIds,
                                 @RequestParam("cityId") String  cityId,
                                 @RequestParam("user_id")String user_id);

    @PostMapping("/product/djBasicsProduct/deleteBasicsProductById")
    @ApiOperation(value = "根据货品id删除商品对象", notes = "根据货品id删除商品对象")
    ServerResponse deleteBasicsProductById(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("id") String id);

    @PostMapping("/product/djBasicsProduct/deleteBasicsGoods")
    @ApiOperation(value = "根据id删除货品及其下面的商品", notes = "根据id删除货品及其下面的商品")
    ServerResponse deleteBasicsGoods(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("id") String id);

    @PostMapping("/product/djBasicsProduct/getGoodsByGid")
    @ApiOperation(value = "根据货品id查询对应货品信息", notes = "根据货品id查询对应货品信息")
    ServerResponse getBasicsGoodsByGid(@RequestParam("cityId") String cityId,
                                 @RequestParam("goodsId") String goodsId);

    @PostMapping("/product/djBasicsProduct/queryProduct")
    @ApiOperation(value = "查询所有商品根据类别ID", notes = "查询所有商品根据类别ID")
    ServerResponse<PageInfo> queryProduct(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("pageDTO") PageDTO pageDTO,
                                          @RequestParam("categoryId") String categoryId,
                                          @RequestParam("cityId") String cityId);

    @PostMapping("/product/djBasicsProduct/queryUnit")
    @ApiOperation(value = "查询所有单位", notes = "查询所有单位")
    ServerResponse queryUnit(@RequestParam("request") HttpServletRequest request,
                             @RequestParam("cityId") String cityId);

    @PostMapping("/product/djBasicsProduct/getProductById")
    @ApiOperation(value = "根据商品ID查询商品对象", notes = "根据商品ID查询商品对象")
    ServerResponse getProductById(@RequestParam("cityId") String cityId,
                                  @RequestParam("id") String id);



    @PostMapping("/product/djBasicsProduct/getTemporaryStorageProductByGoodsId")
    @ApiOperation(value = "查询货品下暂存的商品信息", notes = "查询货品下暂存的商品信息")
    ServerResponse getTemporaryStorageProductByGoodsId(@RequestParam("cityId") String cityId,
                                  @RequestParam("goodsId") String goodsId);

    /**
     * 根据类别Id查询所属货品
     *
     * @return
     */
    @PostMapping("/product/djBasicsProduct/getAllGoodsByCategoryId")
    @ApiOperation(value = "根据类别Id查询所属货品", notes = "根据类别Id查询所属商品")
    ServerResponse getAllGoodsByCategoryId(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam("categoryId") String categoryId,
                                           @RequestParam("cityId") String cityId);

    /**
     * 根据类别Id查询所属货品
     *
     * @return
     */
    @PostMapping("/product/djBasicsProduct/getAllProductByCategoryId")
    @ApiOperation(value = "根据类别Id查询所属货品", notes = "根据类别Id查询所属货品")
    List<DjBasicsProductTemplate> getAllProductByCategoryId( @RequestParam("categoryId") String categoryId,
                                                             @RequestParam("cityId") String cityId);



    /**
     * 根据货品ID查询商品
     *
     * @param goodsId
     * @return
     */
    @PostMapping("/product/djBasicsProduct/getAllProductByGoodsId")
    @ApiOperation(value = "根据货品ID查询所有商品", notes = "根据货品ID查询所有商品")
    ServerResponse getAllProductByGoodsId(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("goodsId") String goodsId,
                                          @RequestParam("cityId") String cityId);

    @PostMapping("/product/djBasicsProduct/queryGoodsList")
    @ApiOperation(value = "按照name模糊查询商品及下属货品", notes = "按照name模糊查询商品及下属货品，type： 是否禁用  0：禁用；1不禁用 ;  -1全部默认")
    ServerResponse queryGoodsList(@RequestParam("request") HttpServletRequest request,
                                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                                    @RequestParam("categoryId") String categoryId,
                                                    @RequestParam("name") String name,
                                                    @RequestParam("cityId") String cityId,
                                                    @RequestParam("type") Integer type);

    @PostMapping("/product/djBasicsProduct/queryGoodsListStorefront")
    @ApiOperation(value = "按照name模糊查询商品及下属货品", notes = "按照name模糊查询商品及下属货品，type： 是否禁用  0：禁用；1不禁用 ;  -1全部默认")
    ServerResponse queryGoodsListStorefront(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("pageDTO") PageDTO pageDTO,
                                  @RequestParam("categoryId") String categoryId,
                                  @RequestParam("name") String name,
                                  @RequestParam("cityId") String cityId,
                                  @RequestParam("type") Integer type);

    /**
     * 猜你喜欢(商品详情列表，随机返回同类别下的12个商品)
     * @param request
     * @param goodsId
     * @return
     * chenyufeng
     */
    @PostMapping("app/product/randQueryProduct")
    @ApiOperation(value = "猜你喜欢(商品详情列表，随机返回同类别下的12个商品)", notes = "猜你喜欢(商品详情列表，随机返回同类别下的12个商品)")
    ServerResponse randQueryProduct(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("goodsId") String goodsId);

    @PostMapping("app/product/djBasicsProduct/queryBasicsProductData")
    @ApiOperation(value = "商品库检索查询", notes = "商品库检索查询")
    PageInfo queryBasicsProductData(@RequestParam("cityId") String cityId,
                                    @RequestParam("pageNum") Integer pageNum,
                                    @RequestParam("pageSize") Integer pageSize,
                                    @RequestParam("name") String name,
                                    @RequestParam("categoryId") String categoryId,
                                    @RequestParam("productType") String productType,
                                    @RequestParam("productId") String[] productId);


    /**
     * 确认开工页选择商铺列表
     * @param request
     * @return
     */
    @PostMapping("web/product/queryChooseGoods")
    @ApiOperation(value = "确认开工页选择商品列表", notes = "确认开工页选择商品列表")
    ServerResponse queryChooseGoods(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("cityId")String cityId);




    @PostMapping("/product/djBasicsProduct/queryProductLabelsByProductId")
    @ApiOperation(value = "根据商品id查询标签", notes = "根据商品id查询标签")
    ServerResponse queryProductLabelsByProductId(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("productId") String productId);

    @PostMapping("/app/product/djBasicsProduct/queryAllWorkerProductList")
    @ApiOperation(value = "查询所有的人工商品", notes = "查询所有的人工商品")
    ServerResponse queryAllWorkerProductList(@RequestParam("request")HttpServletRequest request,
                                    @RequestParam("name")String name,
                                    @RequestParam("cityId")String cityId);

    @PostMapping("/app/product/djBasicsProduct/queryProductAddRelationByPid")
    @ApiOperation(value = "根据货品id查询增值商品关联关系表", notes = "根据货品id查询增值商品关联关系表")
    List<ProductAddedRelation> queryProductAddRelationByPid(@RequestParam("request")HttpServletRequest request,
                                                      @RequestParam("pid")String pid);
}
