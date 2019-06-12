package com.dangjia.acg.api.basics;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(description = "货品管理接口")
@FeignClient("dangjia-service-goods")
public interface ProductAPI {

    @PostMapping("/basics/product/queryProduct")
    @ApiOperation(value = "查询所有货品", notes = "查询所有货品")
    ServerResponse<PageInfo> queryProduct(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("pageDTO") PageDTO pageDTO,
                                          @RequestParam("categoryId") String categoryId);

    @PostMapping("/basics/product/queryUnit")
    @ApiOperation(value = "查询所有单位", notes = "查询所有单位")
    ServerResponse queryUnit(@RequestParam("request") HttpServletRequest request);

    @PostMapping("/basics/product/queryBrand")
    @ApiOperation(value = "查询所有品牌", notes = "查询所有品牌")
    ServerResponse queryBrand(@RequestParam("request") HttpServletRequest request);

    @PostMapping("/basics/product/queryBrandSeries")
    @ApiOperation(value = "根据品牌查询所有品牌系列", notes = "根据品牌查询所有品牌系列")
    ServerResponse queryBrandSeries(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("brandId") String brandId);

    @PostMapping("/basics/product/saveGoods")
    @ApiOperation(value = "新增商品", notes = "新增商品")
    ServerResponse saveGoods(@RequestParam("request") HttpServletRequest request,
                             @RequestParam("name") String name,
                             @RequestParam("categoryId") String categoryId,
                             @RequestParam("buy") Integer buy,
                             @RequestParam("sales") Integer sales,
                             @RequestParam("unitId") String unitId,
                             @RequestParam("type") Integer type,
                             @RequestParam("arrString") String arrString,
                             @RequestParam("otherName") String otherName);

    @PostMapping("/basics/product/queryBrandByGid")
    @ApiOperation(value = "根据商品id查询关联品牌", notes = "根据商品id查询关联品牌")
    ServerResponse queryBrandByGid(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("goodsId") String goodsId);

    @PostMapping("/basics/product/queryBrandByGidAndBid")
    @ApiOperation(value = "根据商品id和品牌id查询关联品牌系列", notes = "根据商品id和品牌id查询关联品牌系列")
    ServerResponse queryBrandByGidAndBid(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("goodsId") String goodsId,
                                         @RequestParam("brandId") String brandId);

    @PostMapping("/basics/product/updateProduct")
    @ApiOperation(value = "更新货品名称", notes = "更新货品名称")
    ServerResponse updateProductById(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("id") String id,
                                     @RequestParam("name") String name);

    @PostMapping("/basics/product/insertProduct")
    @ApiOperation(value = "新增货品", notes = "新增货品")
    ServerResponse insertProduct(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("productArr") String productArr);

    @PostMapping("/basics/product/getGoodsByGid")
    @ApiOperation(value = "根据商品id查询对应商品", notes = "根据商品id查询对应商品")
    ServerResponse getGoodsByGid(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("goodsId") String goodsId);

    @PostMapping("/basics/product/updateGoods")
    @ApiOperation(value = "修改商品", notes = "修改商品")
    ServerResponse updateGoods(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("id") String id,
                               @RequestParam("name") String name,
                               @RequestParam("categoryId") String categoryId,
                               @RequestParam("buy") Integer buy,
                               @RequestParam("sales") Integer sales,
                               @RequestParam("unitId") String unitId,
                               @RequestParam("type") Integer type,
                               @RequestParam("arrString") String arrString,
                               @RequestParam("otherName") String otherName);

    @PostMapping("/basics/product/getProductById")
    @ApiOperation(value = "根据货品id查询货品对象", notes = "根据货品id查询货品对象")
    ServerResponse getProductById(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("id") String id);

    @PostMapping("/basics/product/deleteProductById")
    @ApiOperation(value = "根据货品id删除货品对象", notes = "根据货品id删除货品对象")
    ServerResponse deleteProductById(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("id") String id);

    @PostMapping("/basics/product/deleteGoods")
    @ApiOperation(value = "根据id删除商品和下属货品", notes = "根据id删除商品和下属货品")
    ServerResponse deleteGoods(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("id") String id);

    @PostMapping("/basics/product/queryGoodsListByCategoryLikeName")
    @ApiOperation(value = "按照name模糊查询商品及下属货品", notes = "按照name模糊查询商品及下属货品，type： 是否禁用  0：禁用；1不禁用 ;  -1全部默认")
    ServerResponse queryGoodsListByCategoryLikeName(@RequestParam("request") HttpServletRequest request,
                                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                                    @RequestParam("categoryId") String categoryId,
                                                    @RequestParam("name") String name,
                                                    @RequestParam("cityId") String cityId,
                                                    @RequestParam("type") Integer type);

    @PostMapping("/basics/product/updateProductLabelList")
    @ApiOperation(value = "批量添加/修改货品标签", notes = "批量添加/修改货品标签")
    ServerResponse updateProductLabelList(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("productLabeList") String productLabeList);

    @PostMapping("/basics/product/queryProductListByGoodsIdAndLabelId")
    @ApiOperation(value = "根据商品id和标签id ，查出对应的货品对象集合", notes = "根据商品id和标签id ，查出对应的货品对象集合")
    ServerResponse queryProductListByGoodsIdAndLabelId(@RequestParam("request") HttpServletRequest request,
                                                       @RequestParam("jsonArr") String goodsArr,
                                                       @RequestParam("labelId") String labelId);

    @PostMapping("/basics/product/data")
    @ApiOperation(value = "商品库检索查询", notes = "商品库检索查询")
    PageInfo queryProductData(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("pageNum") Integer pageNum,
                              @RequestParam("pageSize") Integer pageSize,
                              @RequestParam("name") String name,
                              @RequestParam("categoryId") String categoryId,
                              @RequestParam("productType") String productType,
                              @RequestParam("productId") String[] productId);

	/*@PostMapping("/basics/product/getSwitchProduct")
	@ApiOperation(value = "根据系列和属性查询切换货品", notes = "根据系列和属性查询切换货品")
	  ServerResponse getSwitchProduct(@RequestParam("request")HttpServletRequest request,@RequestParam("brandSeriesId")String brandSeriesId,
											@RequestParam("attributeIdArr")String attributeIdArr);*/

//	@PostMapping("/basics/product/updateProductByProductId")
//    @ApiOperation(value = "修改商品库内商品,全局更新", notes ="修改商品库内商品,全局更新")
//	ServerResponse updateProductByProductId(@RequestParam("request") HttpServletRequest request,
//                                            @RequestParam("id") String id,
//                                            @RequestParam("categoryId") String  categoryId,
//                                            @RequestParam("brandSeriesId") String brandSeriesId,
//                                            @RequestParam("brandId") String brandId,
//                                            @RequestParam("name") String name,
//                                            @RequestParam("unitId") String unitId,
//                                            @RequestParam("unitName") String unitName);
}
