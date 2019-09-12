package com.dangjia.acg.api.product;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.BasicsGoodsDTO;
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

    @PostMapping("/product/djBasicsProduct/queryProductLabels")
    @ApiOperation(value = "查询商品标签", notes = "查询商品标签")
    ServerResponse queryProductLabels(@RequestParam("request")HttpServletRequest request,
                                      @RequestParam("productId")String productId);

    @PostMapping("/product/djBasicsProduct/addLabelsValue")
    @ApiOperation(value = "商品打标签", notes = "商品打标签")
    ServerResponse addLabelsValue(@RequestParam("request")HttpServletRequest request,
                                  @RequestParam("jsonStr")String jsonStr);

    @PostMapping("/product/djBasicsProduct/saveBasicsGoods")
    @ApiOperation(value = "新增货品", notes = "新增货品")
    ServerResponse saveBasicsGoods(@RequestParam("request") HttpServletRequest request,
                                   BasicsGoodsDTO basicsGoodsDTO);

    @PostMapping("/product/djBasicsProduct/insertProduct")
    @ApiOperation(value = "新增货品下的商品", notes = "新增货品下的商品")
    ServerResponse insertProduct(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("productArr") String productArr);
}
