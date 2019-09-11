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
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@Api(description = "商品标签接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsLabelAPI {

    @PostMapping("/product/djBasicsLabel/addCommodityLabels")
    @ApiOperation(value = "添加商品标签", notes = "添加商品标签")
    ServerResponse addCommodityLabels(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("labelName") String labelName,
                                      @RequestParam("labelValue") String labelValue);

    @PostMapping("/product/djBasicsLabel/updateCommodityLabels")
    @ApiOperation(value = "编辑商品标签", notes = "编辑商品标签")
    ServerResponse updateCommodityLabels(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("id") String id,
                                         @RequestParam("labelName") String labelName,
                                         @RequestParam("labelValue") String labelValue);

    @PostMapping("/product/djBasicsLabel/delCommodityLabels")
    @ApiOperation(value = "删除商品标签", notes = "删除商品标签")
    ServerResponse delCommodityLabels(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("id") String id);


    @PostMapping("/product/djBasicsLabel/queryCommodityLabelsById")
    @ApiOperation(value = "根据id查询标签", notes = "根据id查询标签")
    ServerResponse queryCommodityLabelsById(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("id") String id);

    @PostMapping("/product/djBasicsLabel/queryCommodityLabels")
    @ApiOperation(value = "查询标签", notes = "查询标签")
    ServerResponse queryCommodityLabels(@RequestParam("request") HttpServletRequest request);

}
