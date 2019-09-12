package com.dangjia.acg.api.product;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: LJL
 * Date: 2019/9/11
 * Time: 13:56
 */
@Api(description = "商品表接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsProductAPI {

    @PostMapping("/product/djBasicsProduct/queryProductData")
    @ApiOperation(value = "查询商品信息", notes = "查询商品信息")
    ServerResponse queryProductData(@RequestParam("request")HttpServletRequest request,
                                    @RequestParam("name")String name);

    @PostMapping("/product/djBasicsProduct/saveBasicsGoods")
    @ApiOperation(value = "新增货品", notes = "新增货品")
    ServerResponse saveBasicsGoods(@RequestParam("request") HttpServletRequest request,
                             @RequestParam("name") String name,
                             @RequestParam("categoryId") String categoryId,
                             @RequestParam("buy") Integer buy,
                             @RequestParam("sales") Integer sales,
                             @RequestParam("unitId") String unitId,
                             @RequestParam("type") Integer type,
                             @RequestParam("arrString") String arrString,
                             @RequestParam("otherName") String otherName);
}
