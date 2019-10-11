package com.dangjia.acg.api.supplier;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supplier.DjSupplier;
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
@Api(description = "供应商管理接口")
@FeignClient("dangjia-service-master")
public interface DjSupplierAPI {

    @PostMapping("/supplier/djSupplier/updateBasicInformation ")
    @ApiOperation(value = "供应商基础信息维护", notes = "供应商基础信息维护")
    ServerResponse updateBasicInformation(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("djSupplier") DjSupplier djSupplier);

    @PostMapping("/supplier/djSupplier/selectSupplyList ")
    @ApiOperation(value = "选择供货列表", notes = "选择供货列表")
    ServerResponse querySupplyList(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("pageDTO") PageDTO pageDTO,
                                    @RequestParam("supId") String supId,
                                    @RequestParam("searchKey") String searchKey);


    @PostMapping("/supplier/djSupplier/querySupplierGoods ")
    @ApiOperation(value = "供应商商品列表", notes = "供应商商品列表")
    ServerResponse querySupplierGoods(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("pageDTO") PageDTO pageDTO,
                                      @RequestParam("supId") String supId);
}
