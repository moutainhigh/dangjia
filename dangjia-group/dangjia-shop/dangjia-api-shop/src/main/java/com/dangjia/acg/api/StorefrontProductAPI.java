package com.dangjia.acg.api;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.MemberCollectDTO;
import com.dangjia.acg.dto.product.ShoppingCartProductDTO;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.dto.storefront.StorefrontProductListDTO;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductDTO;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api(description = "店铺商品管理接口")
@FeignClient("dangjia-service-shop")
public interface StorefrontProductAPI {

//    @PostMapping("/web/queryStorefrontListByStorefrontId")
//    @ApiOperation(value = "根据店铺id查询供应商商品", notes = "根据店铺id查询供应商商品")
//    List<StorefrontDTO> queryStorefrontListByStorefrontId(@RequestParam("storefrontId") String storefrontId,
//                                                          @RequestParam("searchKey") String searchKey);
    @PostMapping("/web/countStorefrontProduct")
    @ApiOperation(value = "统计上架数", notes = "供货设置-统计上架数")
    ServerResponse countStorefrontProduct(@RequestParam("userId") String userId,@RequestParam("cityId") String cityId);

    @PostMapping("/web/querySingleStorefrontProductById")
    @ApiOperation(value = "根据id查询店铺商品信息", notes = "根据id查询店铺商品信息")
    StorefrontProductListDTO querySingleStorefrontProductById(@RequestParam("id") String id);

    @PostMapping("/web/addStorefrontProduct")
    @ApiOperation(value = "增加已选商品", notes = "供货设置-增加已选商品")
    ServerResponse addStorefrontProduct(BasicsStorefrontProductDTO basicsStorefrontProductDTO);

    @PostMapping("/web/delStorefrontProductById")
    @ApiOperation(value = "供货设置-删除已选商品", notes = "供货设置-删除已选商品")
    ServerResponse delStorefrontProductById(@RequestParam("id") String id);

    @PostMapping("/web/delProductByProIdAndStoreIdAndCityId")
    @ApiOperation(value = "供货设置-根据货品id，城市id，店铺id删除店铺商品", notes = "供货设置-根据货品id，城市id，店铺id删除店铺商品")
    ServerResponse delProductByProIdAndStoreIdAndCityId(@RequestParam("productId") String productId,
                                                        @RequestParam("userId") String userId, @RequestParam("cityId") String cityId);

    @PostMapping("/web/queryStorefrontProductByKeyWord")
    @ApiOperation(value = "供货设置-上架商品-通过货品或者商品名称查询", notes = "供货设置-上架商品-通过货品或者商品名称查询")
    ServerResponse queryStorefrontProductByKeyWord(
            @RequestParam("keyWord") String keyWord,@RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("cityId") String cityId);

    @PostMapping("/web/queryStorefrontProductGroundByKeyWord")
    @ApiOperation(value = "供货设置-已选商品-通过货品或者商品名称查询", notes = "供货设置-已选商品-通过货品或者商品名称查询")
    ServerResponse queryStorefrontProductGroundByKeyWord(
            @RequestParam("keyWord") String keyWord,@RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("cityId") String cityId);


    @PostMapping("/web/queryProductAdjustmentPriceListByKeyWord")
    @ApiOperation(value = "供货设置-上架商品-调价列表", notes = "供货设置-上架商品-调价列表")
    ServerResponse queryProductAdjustmentPriceListByKeyWord(
            @RequestParam("keyWord") String keyWord,@RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("cityId") String cityId);


    @PostMapping("/web/fixModityPrice")
    @ApiOperation(value = "供货设置-上架商品-调价列表-确定调价", notes = "供货设置-上架商品-调价列表-确定调价")
    ServerResponse fixModityPrice(
            @RequestParam("storefrontProductList") String storefrontProductList,@RequestParam("userId") String userId,
            @RequestParam("pageDTO") PageDTO pageDTO,@RequestParam("cityId") String cityId);

    @PostMapping("/web/setSpStatusById")
    @ApiOperation(value = "供货设置-设置商品上下架", notes = "设置商品上下架")
    ServerResponse setSpStatusById( @RequestParam("userId") String userId,
                                    @RequestParam("cityId") String cityId,@RequestParam("id") String id, @RequestParam("isShelfStatus") String isShelfStatus);

    @PostMapping("/web/setAllStoreProductByIsShelfStatus")
    @ApiOperation(value = "供货设置-设置商品批量上下架", notes = "设置商品批量上下架")
    ServerResponse setAllStoreProductByIsShelfStatus(@RequestParam("userId") String userId,
                                                     @RequestParam("cityId") String cityId,
                                                     @RequestParam("id") String id, @RequestParam("isShelfStatus") String isShelfStatus);

    @PostMapping("/web/saveStorefrontProductById")
    @ApiOperation(value = "供货设置-保存编辑店铺商品", notes = "供货设置-保存编辑店铺商品")
    ServerResponse saveStorefrontProductById(@RequestParam("storefrontProduct") StorefrontProduct storefrontProduct);

    @PostMapping("/web/queryCartList")
    @ApiOperation(value = "查询购物车商品信息", notes = "查询购物车商品信息")
    List<ShoppingCartProductDTO> queryCartList(@RequestParam("storefrontId") String storefrontId,
                                               @RequestParam("productId") String productId);

    @PostMapping("/web/queryCollectGood")
    @ApiOperation(value = "查询收藏商品", notes = "查询收藏商品")
    List<MemberCollectDTO> queryCollectGood(@RequestParam("productId") String productId);


    @PostMapping("/sup/priceAdjustmentTask")
    @ApiOperation(value = "店铺商品调价任务", notes = "店铺商品调价任务")
    void priceAdjustmentTask();
}
