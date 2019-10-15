package com.dangjia.acg.api.actuary;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.misc.Request;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/9/20
 * Time: 16:31
 */
@Api(description = "精算配置默认展示接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsActuarialConfigurationAPI {


    @PostMapping("web/config/actuarialConfig/queryActuarialTemplateConfig")
    @ApiOperation(value = "查询设计精算阶段配置", notes = "查询设计精算阶段配置")
    ServerResponse queryActuarialTemplateConfig(@RequestParam("request") HttpServletRequest request);

    @PostMapping("web/config/actuarialConfig/queryActuarialTemplateConfigById")
    @ApiOperation(value = "查询阶段详情信息", notes = "查询阶段详情信息")
    ServerResponse queryActuarialProductByConfigId(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("actuarialTemplateId") String actuarialTemplateId);

    @PostMapping("web/config/actuarialConfig/editActuarialProduct")
    @ApiOperation(value = "批量添加修改设计精算阶段对应的商品信息", notes = "批量添加修改设计精算阶段对应的商品信息")
    ServerResponse editActuarialProduct(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("actuarialProductStr") String actuarialProductStr,
                                        @RequestParam("userId") String userId);

    @PostMapping("web/config/actuarialConfig/deleteActuarialProduct")
    @ApiOperation(value = "删除对应的设计精算配置商品", notes = "删除对应的设计精算配置商品")
    ServerResponse deleteActuarialProduct(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("id") String id);

    @PostMapping("web/config/actuarialConfig/getActuarialGoodsList")
    @ApiOperation(value = "查询对应设计精算的货品列表", notes = "查询对应设计精算的货品列表")
    ServerResponse getActuarialGoodsList(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("configType") String configType);
}
