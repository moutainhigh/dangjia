package com.dangjia.acg.api.supplier;

import com.dangjia.acg.common.model.PageDTO;
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
 * Date: 8/10/2019
 * Time: 下午 2:03
 */
@Api(description = "供应店铺关系单表管理接口")
@FeignClient("dangjia-service-store")
public interface DjSupApplicationAPI {


    @PostMapping("/sup/djSupApplication/queryDjSupApplicationProductByShopID")
    @ApiOperation(value = "店铺-审核供货列表", notes = "店铺-审核供货列表")
    ServerResponse queryDjSupApplicationProductByShopID(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("pageDTO") PageDTO pageDTO,
                                                 @RequestParam("keyWord") String keyWord,
                                                 @RequestParam("shopId") String shopId);

    @PostMapping("/supplier/djSupApplication/insertSupplierApplicationShop")
    @ApiOperation(value = "供应商申请供应店铺", notes = "供应商申请供应店铺")
    ServerResponse insertSupplierApplicationShop(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("userId") String userId,
                                                 @RequestParam("cityId") String cityId,
                                                 @RequestParam("shopId") String shopId);

    @PostMapping("/supplier/djSupApplication/uploadContracts")
    @ApiOperation(value = "上传合同", notes = "上传合同")
    ServerResponse uploadContracts(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("id") String id,
                                   @RequestParam("contract") String contract);

    @PostMapping("/supplier/djSupApplication/queryContracts")
    @ApiOperation(value = "查看合同", notes = "查看合同")
    ServerResponse queryContracts(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("id") String id);

}
