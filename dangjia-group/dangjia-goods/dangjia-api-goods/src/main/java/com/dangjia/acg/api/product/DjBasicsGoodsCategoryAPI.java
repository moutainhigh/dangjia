package com.dangjia.acg.api.product;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/15
 * Time: 10:00
 */
@Api(description = "服务类别管理接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsGoodsCategoryAPI {

    @PostMapping("/product/djBasicsGoodsCategory/addGoodsCategory")
    @ApiOperation(value = "新增商品类别", notes = "新增商品类别")
    ServerResponse addGoodsCategory(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("name") String name,
                                    @RequestParam("parentId") String parentId,
                                    @RequestParam("parentTop") String parentTop,
                                    @RequestParam("sort") Integer sort,
                                    @RequestParam("isLastCategory") Integer isLastCategory,
                                    @RequestParam("categoryLabelId") String categoryLabelId,
                                    @RequestParam("coverImage") String coverImage,
                                    @RequestParam("purchaseRestrictions") Integer purchaseRestrictions,
                                    @RequestParam("brandId") String brandId);

    @PostMapping("/product/djBasicsGoodsCategory/updateGoodsCategory")
    @ApiOperation(value = "修改商品类别", notes = "修改商品类别")
    ServerResponse updateGoodsCategory(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("id") String id,
                                       @RequestParam("name") String name,
                                       @RequestParam("parentId") String parentId,
                                       @RequestParam("parentTop") String parentTop,
                                       @RequestParam("sort") Integer sort,
                                       @RequestParam("isLastCategory") Integer isLastCategory,
                                       @RequestParam("categoryLabelId") String categoryLabelId,
                                       @RequestParam("coverImage") String coverImage,
                                       @RequestParam("purchaseRestrictions") Integer purchaseRestrictions,
                                       @RequestParam("brandId") String brandId);

    @PostMapping("/product/djBasicsGoodsCategory/queryGoodsCategory")
    @ApiOperation(value = "查询商品类别列表", notes = "查询商品类别列表")
    ServerResponse queryGoodsCategory(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("parentId") String parentId);

    @PostMapping("/product/djBasicsGoodsCategory/addGoodsAttribute")
    @ApiOperation(value = "新增属性及其属性选项", notes = "新增属性及其属性选项")
    ServerResponse addGoodsAttribute(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("goodsCategoryId") String goodsCategoryId,
                                     @RequestParam("attributeName") String attributeName,
                                     @RequestParam("type") Integer type,
                                     @RequestParam("jsonStr") String jsonStr,
                                     @RequestParam("isScreenConditions") Integer isScreenConditions);

    @PostMapping("/product/djBasicsGoodsCategory/updateGoodsAttribute")
    @ApiOperation(value = "修改属性及其属性选项", notes = "修改属性及其属性选项")
    ServerResponse updateGoodsAttribute(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("attributeId") String attributeId,
                                        @RequestParam("attributeName") String attributeName,
                                        @RequestParam("type") Integer type,
                                        @RequestParam("jsonStr") String jsonStr,
                                        @RequestParam("isScreenConditions") Integer isScreenConditions);

    @PostMapping("/product/djBasicsGoodsCategory/deleteByAttributeId")
    @ApiOperation(value = "删除商品属性选项", notes = "删除商品属性选项")
    ServerResponse deleteByAttributeId(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("attributeValueId") String attributeValueId);

    @PostMapping("/product/djBasicsGoodsCategory/queryAttributeListById")
    @ApiOperation(value = "查询类别id查询所有父级以及父级属性", notes = "查询类别id查询所有父级以及父级属性")
    ServerResponse queryAttributeListById(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("goodsCategoryId") String goodsCategoryId);

    @PostMapping("/product/djBasicsGoodsCategory/queryGoodsCategoryTwo")
    @ApiOperation(value = "查询两级商品分类", notes = "查询两级商品分类")
    ServerResponse queryGoodsCategoryTwo(@RequestParam("request") HttpServletRequest request);

}
