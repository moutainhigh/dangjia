package com.dangjia.acg.api.supplier;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supplier.DjSupApplication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:03
 */
@Api(description = "供应店铺关系单表管理接口")
@FeignClient("dangjia-service-goods")
public interface DjSupApplicationAPI {

    @PostMapping("/sup/djSupApplication/queryDjSupApplicationByShopID")
    @ApiOperation(value = "根据店铺ID查询申请供应商列表", notes = "根据店铺ID查询申请供应商列表")
    ServerResponse queryDjSupApplicationByShopID(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("pageDTO") PageDTO pageDTO, @RequestParam("shopId") String shopId);

    @PostMapping("/supplier/djSupplier/insertSupplierApplicationShop ")
    @ApiOperation(value = "供应商申请供应店铺", notes = "供应商申请供应店铺")
    ServerResponse insertSupplierApplicationShop(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("supId") String supId,
                                                 @RequestParam("shopId") String shopId);


}
