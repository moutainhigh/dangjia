package com.dangjia.acg.api.supplier;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(description = "供应商申请商品表")
@FeignClient("dangjia-service-store")
public interface DjSupApplicationProductAPI {


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
                                      @RequestParam("applicationStatus") String applicationStatus);


    @PostMapping("/web/rejectAllProduct")
    @ApiOperation(value = "店铺-审核供货列表-全部打回", notes = "店铺-审核供货列表-全部打回")
    ServerResponse rejectAllProduct(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("id") String  id);



    @PostMapping("/web/rejectPartProduct")
    @ApiOperation(value = "店铺-审核供货列表-部分通过", notes = "店铺-审核供货列表-部分通过")
    ServerResponse rejectPartProduct(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("id") String id);

    @PostMapping("/supplier/djSupApplicationProduct/querySupplierGoods")
    @ApiOperation(value = "供应商申请供应商品", notes = "供应商申请供应商品")
    ServerResponse insertDjSupApplicationProduct(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("jsonStr") String jsonStr);

    @PostMapping("/supplier/djSupApplicationProduct/queryHaveGoods")
    @ApiOperation(value = "查询已供商品", notes = "查询已供商品")
    ServerResponse queryHaveGoods(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("supId") String supId,
                                  @RequestParam("shopId") String shopId,
                                  @RequestParam("applicationStatus")String applicationStatus ,
                                  @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("/supplier/djSupApplicationProduct/updateHaveGoods")
    @ApiOperation(value = "编辑已供商品", notes = "编辑已供商品")
    ServerResponse updateHaveGoods(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("jsonStr") String jsonStr);

}
