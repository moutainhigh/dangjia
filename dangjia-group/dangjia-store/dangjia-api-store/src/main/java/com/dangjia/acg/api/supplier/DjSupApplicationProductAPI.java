package com.dangjia.acg.api.supplier;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.delivery.SupplyDimensionDTO;
import com.dangjia.acg.modle.supplier.DjSupApplicationProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(description = "供应商申请商品接口")
@FeignClient("dangjia-service-store")
public interface DjSupApplicationProductAPI {

    @PostMapping("/web/queryDjSupSupplierProductList")
    @ApiOperation(value = "根据供应商id查询供应商商品", notes = "根据供应商id查询供应商商品")
    List<SupplyDimensionDTO> queryDjSupSupplierProductList(@RequestParam("supId") String supId,
                                                           @RequestParam("searchKey") String searchKey);

    @PostMapping("/web/getExaminedProduct")
    @ApiOperation(value = "店铺-审核供货列表-根据供应商名称或号码", notes = "店铺-审核供货列表-根据供应商名称或号码")
    ServerResponse getExaminedProduct(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("applicationStatus") String applicationStatus,
                                      @RequestParam("shopId") String shopId,
                                      @RequestParam("keyWord") String keyWord);


    @PostMapping("/web/getSuppliedProduct")
    @ApiOperation(value = "店铺-审核供货列表-已供商品", notes = "店铺-审核供货列表-已供商品")
    ServerResponse getSuppliedProduct(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("supId") String supId,
                                      @RequestParam("shopId") String shopId,
                                      @RequestParam("applicationStatus") String applicationStatus,
                                      @RequestParam("keyWord") String keyWord);


    @PostMapping("/web/rejectAllProduct")
    @ApiOperation(value = "店铺-审核供货列表-全部打回", notes = "店铺-审核供货列表-全部打回")
    ServerResponse rejectAllProduct(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("id") String  id);



    @PostMapping("/web/rejectPartProduct")
    @ApiOperation(value = "店铺-审核供货列表-部分通过", notes = "店铺-审核供货列表-部分通过")
    ServerResponse rejectPartProduct(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("id") String id);

    @PostMapping("/supplier/djSupApplicationProduct/queryNotForTheGoods")
    @ApiOperation(value = "查询未供商品", notes = "查询未供商品")
    ServerResponse queryNotForTheGoods(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("supId") String supId,
                                       @RequestParam("shopId") String shopId,
                                       @RequestParam("pageDTO") PageDTO pageDTO,  @RequestParam("keyWord") String keyWord);

    @PostMapping("/supplier/djSupApplicationProduct/querySupplierGoods")
    @ApiOperation(value = "供应商申请供应商品", notes = "供应商申请供应商品")
    ServerResponse insertDjSupApplicationProduct(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("jsonStr") String jsonStr,
                                                 @RequestParam("cityId") String cityId,
                                                 @RequestParam("supId") String supId,
                                                 @RequestParam("shopId") String shopId);

    @PostMapping("/supplier/djSupApplicationProduct/queryHaveGoods")
    @ApiOperation(value = "查询已供商品", notes = "查询已供商品")
    ServerResponse queryHaveGoods(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("supId") String supId,
                                  @RequestParam("shopId") String shopId,
                                  @RequestParam("applicationStatus")String applicationStatus ,
                                  @RequestParam("pageDTO") PageDTO pageDTO,
                                  @RequestParam("keyWord") String keyWord);

    @PostMapping("/supplier/djSupApplicationProduct/updateHaveGoods")
    @ApiOperation(value = "编辑已供商品", notes = "编辑已供商品")
    ServerResponse updateHaveGoods(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("jsonStr") String jsonStr,
                                   @RequestParam("userId") String userId);

    @PostMapping("/supplier/djSupApplicationProduct/updateReapply")
    @ApiOperation(value = "被打回商品申请", notes = "被打回商品申请")
    ServerResponse updateReapply(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("jsonStr") String jsonStr);

    @PostMapping("/supplier/djSupApplicationProduct/setCommodityPricing")
    @ApiOperation(value = "商品调价定时任务", notes = "商品调价定时任务")
    void setCommodityPricing();




    @PostMapping("/supplier/djSupApplicationProduct/supplierList")
    @ApiOperation(value = "新版-查询供应商", notes = "新版-查询供应商")
    ServerResponse supplierList(@RequestParam("cityId") String cityId,
                                @RequestParam("userId") String userId,
                                @RequestParam("productId") String productId);

    @PostMapping("/supplier/djSupApplicationProduct/getDjSupApplicationProduct")
    @ApiOperation(value = "查询供应商商品", notes = "查询供应商商品")
    DjSupApplicationProduct getDjSupApplicationProduct(@RequestParam("cityId") String cityId,
                                                       @RequestParam("supplierId") String supplierId,
                                                       @RequestParam("productId") String productId);
}
