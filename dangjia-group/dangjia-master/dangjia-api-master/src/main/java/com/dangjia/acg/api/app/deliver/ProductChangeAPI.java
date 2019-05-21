package com.dangjia.acg.api.app.deliver;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Yinjianbo
 * Date: 2019-5-11
 */
@FeignClient("dangjia-service-master")
@Api(value = "商品换货操作", description = "商品换货操作")
public interface ProductChangeAPI {


    @PostMapping("app/deliver/productChange/insertProductChange")
    @ApiOperation(value = "添加更换商品", notes = "添加更换商品")
    ServerResponse insertProductChange(HttpServletRequest request, String userToken, String houseId, String srcProductId, String destProductId, Double srcSurCount, Integer productType);

    @PostMapping("app/deliver/productChange/queryChangeByHouseId")
    @ApiOperation(value = "根据houseId查询更换商品列表", notes = "根据houseId查询更换商品列表")
    ServerResponse queryChangeByHouseId(HttpServletRequest request, String userToken, String houseId);

    @PostMapping("app/deliver/productChange/applyProductChange")
    @ApiOperation(value = "申请换货", notes = "申请换货")
    ServerResponse applyProductChange(HttpServletRequest request,String houseId);

    @PostMapping("app/deliver/productChange/setDestSurCount")
    @ApiOperation(value = "设置商品更换数", notes = "设置商品更换数")
    ServerResponse setDestSurCount(HttpServletRequest request, String id, Double destSurCount, String orderId);

    @PostMapping("app/deliver/productChangeOrder/queryOrderByHouseId")
    @ApiOperation(value = "根据houseId查询更换商品订单", notes = "根据houseId查询更换商品订单")
    ServerResponse queryOrderByHouseId(HttpServletRequest request, String houseId);

    @PostMapping("app/deliver/productChangeOrder/orderBackFun")
    @ApiOperation(value = "补退差价回调", notes = "补退差价回调")
    ServerResponse orderBackFun(HttpServletRequest request, String id);
}
