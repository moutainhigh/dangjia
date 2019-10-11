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
    @ApiOperation(value = "查询待审核的供应商品", notes = "查询待审核的供应商品")
    ServerResponse getExaminedProduct(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("supId") String supId,
                                                 @RequestParam("shopId") String shopId);


    @PostMapping("/web/getSuppliedProduct")
    @ApiOperation(value = "已供商品", notes = "已供商品")
    ServerResponse getSuppliedProduct(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("supId") String supId,
                                                 @RequestParam("shopId") String shopId);


    @PostMapping("/web/rejectAllProduct")
    @ApiOperation(value = "全部打回", notes = "全部打回")
    ServerResponse rejectAllProduct(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("supId") String supId,
                                                 @RequestParam("shopId") String shopId);


    @PostMapping("/web/rejectPartProduct")
    @ApiOperation(value = "部分通过", notes = "部分通过")
    ServerResponse rejectPartProduct(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("supId") String supId,
                                                 @RequestParam("shopId") String shopId);

}
