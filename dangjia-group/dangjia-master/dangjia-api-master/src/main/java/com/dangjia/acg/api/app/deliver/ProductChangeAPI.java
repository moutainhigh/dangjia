package com.dangjia.acg.api.app.deliver;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.deliver.ProductChange;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: Yinjianbo
 * Date: 2019-5-11
 */
@FeignClient("dangjia-service-master")
@Api(value = "商品换货操作", description = "商品换货操作")
public interface ProductChangeAPI {


    @PostMapping("app/deliver/productChange/insertProductChange")
    @ApiOperation(value = "添加更换商品", notes = "添加更换商品")
    ServerResponse insertProductChange(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("userToken") String userToken,
                                       @RequestParam("houseId") String houseId,
                                       @RequestParam("srcProductId") String srcProductId,
                                       @RequestParam("destProductId") String destProductId,
                                       @RequestParam("srcSurCount") Double srcSurCount,
                                       @RequestParam("productType") Integer productType);

    @PostMapping("app/deliver/productChange/queryChangeByHouseId")
    @ApiOperation(value = "根据houseId查询更换商品列表", notes = "根据houseId查询更换商品列表")
    ServerResponse queryChangeByHouseId(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("userToken") String userToken,
                                        @RequestParam("houseId") String houseId);

    @PostMapping("app/deliver/productChange/applyProductChange")
    @ApiOperation(value = "申请换货", notes = "申请换货")
    ServerResponse applyProductChange(@RequestParam("request") HttpServletRequest request,@RequestParam("houseId") String houseId);

    @PostMapping("app/deliver/productChange/productSure")
    @ApiOperation(value = "确定", notes = "确定")
    ServerResponse productSure(@RequestParam("request")HttpServletRequest request,@RequestParam("changeItemList") String changeItemList,@RequestParam("orderId") String orderId);

    @PostMapping("app/deliver/productChangeOrder/queryOrderByHouseId")
    @ApiOperation(value = "根据houseId查询更换商品订单", notes = "根据houseId查询更换商品订单")
    ServerResponse queryOrderByHouseId(@RequestParam("request")HttpServletRequest request, @RequestParam("houseId")String houseId);

    @PostMapping("app/deliver/productChangeOrder/orderBackFun")
    @ApiOperation(value = "补退差价回调", notes = "补退差价回调")
    ServerResponse orderBackFun(@RequestParam("request")HttpServletRequest request, @RequestParam("id")String id);

    @PostMapping("web/product/change/goods")
    @ApiOperation(value = "补退差价回调", notes = "补退差价回调")
    List<ProductChange> queryChangeDetail(@RequestParam("houseId")String houseId);
}
