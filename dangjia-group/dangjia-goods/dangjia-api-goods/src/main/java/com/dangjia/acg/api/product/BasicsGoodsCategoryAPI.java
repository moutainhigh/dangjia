package com.dangjia.acg.api.product;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.product.BasicsGoodsCategory;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @类 名： BasicsGoodsCategoryController
 * @功能描述： 商品/服务类别管理
 * @作者信息： fzh
 * @创建时间： 2019-9-11
 */
@Api(description = "商品3.0服务类别管理接口")
@FeignClient("dangjia-service-goods")
public interface BasicsGoodsCategoryAPI {

    @PostMapping("/product/basicsGoodsCategory/getGoodsCategory")
    @ApiOperation(value = "根据类别ID查询商品类别", notes = "根据类别ID查询商品类别")
    BasicsGoodsCategory getGoodsCategory(@RequestParam("cityId") String cityId, @RequestParam("categoryId") String categoryId);


    @PostMapping("/product/basicsGoodsCategory/getBasicsGoodsCategory")
    @ApiOperation(value = "商品类别明细", notes = "商品类别明细")
    ServerResponse getBasicsGoodsCategory(@RequestParam("cityId") String cityId, @RequestParam("categoryId") String categoryId);

    /**
     * 新增商品类别
     *
     * @Title: getProduct
     */
    @PostMapping("/product/basicsGoodsCategory/insertBasicsGoodsCategory")
    @ApiOperation(value = "新增商品类别", notes = "新增商品类别")
    ServerResponse insertBasicsGoodsCategory(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("name") String name,
                                       @RequestParam("parentID") String parentID,
                                       @RequestParam("parentTop") String parentTop,
                                       @RequestParam("sort") Integer sort,
                                       @RequestParam("isLastCategory") String isLastCategory,
                                       @RequestParam("purchaseRestrictions") String purchaseRestrictions,
                                       @RequestParam("brandIds") String brandIds,
                                       @RequestParam("coverImage") String coverImage,
                                       @RequestParam("categoryLabelId") String categoryLabelId);

    /**
     * 修改商品类别
     *
     * @Title: getProduct
     */
    @PostMapping("/product/basicsGoodsCategory/doModifyBasicsGoodsCategory")
    @ApiOperation(value = "修改商品类别", notes = "修改商品类别")
    ServerResponse doModifyBasicsGoodsCategory(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("id") String id,
                                         @RequestParam("name") String name,
                                         @RequestParam("parentID") String parentID,
                                         @RequestParam("parentTop") String parentTop,
                                         @RequestParam("sort") Integer sort,
                                         @RequestParam("isLastCategory") String isLastCategory,
                                         @RequestParam("purchaseRestrictions") String purchaseRestrictions,
                                         @RequestParam("brandIds") String brandIds,
                                         @RequestParam("coverImage") String coverImage,
                                         @RequestParam("categoryLabelId") String categoryLabelId);
    /**
     * 查询商品类别列表
     *
     * @Title: getProduct
     */
    @PostMapping("/product/basicsGoodsCategory/queryBasicsGoodsCategory")
    @ApiOperation(value = "查询商品类别列表", notes = "查询商品类别列表")
    ServerResponse queryBasicsGoodsCategory(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("parentId") String parentId);

    /**
     * 根据类别id查询关联属性
     *
     * @Title: getProduct
     */
    @PostMapping("/product/basicsGoodsCategory/queryBasicsGoodsAttribute")
    @ApiOperation(value = "根据类别id查询关联属性", notes = "根据类别id查询关联属性")
    ServerResponse<PageInfo> queryBasicsGoodsAttribute(@RequestParam("request") HttpServletRequest request,
                                                     @RequestParam("pageDTO") PageDTO pageDTO,
                                                     @RequestParam("goodsCategoryId") String goodsCategoryId,
                                                     @RequestParam("likeAttrName") String likeAttrName);

    /**
     * 根据属性名称模糊查询属性
     *
     * @Title: getProduct
     */
    @PostMapping("/product/basicsGoodsCategory/queryGoodsAttributelikeName")
    @ApiOperation(value = "根据属性名称模糊查询属性", notes = "根据属性名称模糊查询属性")
    ServerResponse<PageInfo> queryGoodsAttributelikeName(@RequestParam("request") HttpServletRequest request,
                                                         @RequestParam("pageDTO") PageDTO pageDTO,
                                                         @RequestParam("name") String name);

    /**
     * 根据属性id查询属性及其下属属性选项
     *
     * @Title: getProduct
     */
    @PostMapping("/product/basicsGoodsCategory/queryAttributeValue")
    @ApiOperation(value = "根据属性id查询属性及其下属属性选项", notes = "根据属性id查询属性及其下属属性选项")
    ServerResponse queryAttributeValue(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("goodsAttributeId") String goodsAttributeId);

    /**
     * 新增属性及其属性选项
     *
     * @Title: getProduct
     */
    @PostMapping("/product/basicsGoodsCategory/addGoodsAttribute")
    @ApiOperation(value = "新增属性及其属性选项", notes = "新增属性及其属性选项")
    ServerResponse addGoodsAttribute(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("goodsCategoryId") String goodsCategoryId,
                                     @RequestParam("attributeName") String attributeName,
                                     @RequestParam("type") Integer type,
                                     @RequestParam("jsonStr") String jsonStr,
                                     @RequestParam("isScreenConditions") Integer isScreenConditions);

    /**
     * 修改属性及其属性选项
     *
     * @Title: getProduct
     */
    @PostMapping("/product/basicsGoodsCategory/updateGoodsAttribute")
    @ApiOperation(value = "修改属性及其属性选项", notes = "修改属性及其属性选项")
    ServerResponse updateGoodsAttribute(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("attributeId") String attributeId,
                                        @RequestParam("attributeName") String attributeName,
                                        @RequestParam("type") Integer type,
                                        @RequestParam("jsonStr") String jsonStr,
                                        @RequestParam("isScreenConditions") Integer isScreenConditions);
    /**
     * 删除商品属性
     */
    @PostMapping("/product/basicsGoodsCategory/deleteGoodsAttribute")
    @ApiOperation(value = "删除商品属性", notes = "删除商品属性")
    ServerResponse deleteGoodsAttribute(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("goodsAttributeId") String goodsAttributeId);

    /**
     * 删除商品属性选项
     */
    @PostMapping("/product/basicsGoodsCategory/deleteByAttributeId")
    @ApiOperation(value = "删除商品属性选项", notes = "删除商品属性选项")
    ServerResponse deleteByAttributeId(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("attributeValueId") String attributeValueId);

    /**
     * 删除商品类别
     */
    @PostMapping("/product/basicsGoodsCategory/deleteBasicsGoodsCategory")
    @ApiOperation(value = "删除商品类别", notes = "删除商品类别")
    ServerResponse deleteBasicsGoodsCategory(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("id") String id);

    /**
     * 查询类别id查询所有父级以及父级属性
     */
    @PostMapping("/product/basicsGoodsCategory/queryAttributeListById")
    @ApiOperation(value = "查询类别id查询所有父级以及父级属性", notes = "查询类别id查询所有父级以及父级属性")
    ServerResponse queryAttributeListById(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("goodsCategoryId") String goodsCategoryId);

    @PostMapping("/product/basicsGoodsCategory/queryGoodsCategoryTwo")
    @ApiOperation(value = "查询两级商品分类", notes = "查询两级商品分类")
    ServerResponse queryGoodsCategoryTwo(@RequestParam("request") HttpServletRequest request);

    @PostMapping("/product/basicsGoodsCategory/queryBrand")
    @ApiOperation(value = "查询所有品牌", notes = "查询所有品牌")
    ServerResponse queryBrand(@RequestParam("request") HttpServletRequest request);

    @PostMapping("/product/basicsGoodsCategory/queryBrandByCategoryId")
    @ApiOperation(value = "查询类别下所有品牌", notes = "查询类别下所有品牌")
    ServerResponse queryBrandByCategoryId(@RequestParam("request") HttpServletRequest request,@RequestParam("categoryId") String categoryId);

    /**
     * 查询商品类别列表
     *
     * @Title: getProduct
     */
    @PostMapping("/product/basicsGoodsCategory/queryGoodsCategoryExistlastCategory")
    @ApiOperation(value = "查询商品类别列表，不包含末级分类类别", notes = "查询商品类别列表，不包含末级分类类别")
    ServerResponse queryGoodsCategoryExistlastCategory(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("parentId") String parentId);

    @PostMapping("/product/basicsGoodsCategory/queryCategoryListByCategoryLikeName")
    @ApiOperation(value = "按照name模糊查询商品及下属货品", notes = "按照name模糊查询商品及下属货品，type： 是否禁用  0：禁用；1不禁用 ;  -1全部默认")
    ServerResponse queryCategoryListByCategoryLikeName(@RequestParam("request") HttpServletRequest request,
                                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                                    @RequestParam("categoryId") String categoryId,
                                                    @RequestParam("name") String name,
                                                    @RequestParam("cityId") String cityId);
}
