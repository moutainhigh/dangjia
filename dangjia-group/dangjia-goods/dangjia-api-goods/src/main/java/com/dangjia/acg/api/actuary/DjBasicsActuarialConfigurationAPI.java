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
    @ApiOperation(value = "设计精算--查询设计精算阶段配置", notes = "查询设计精算阶段配置")
    ServerResponse queryActuarialTemplateConfig(@RequestParam("request") HttpServletRequest request);

    @PostMapping("web/config/actuarialConfig/queryActuarialTemplateConfigById")
    @ApiOperation(value = "设计精算--查询阶段详情信息", notes = "查询阶段详情信息")
    ServerResponse queryActuarialProductByConfigId(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("actuarialTemplateId") String actuarialTemplateId);

    @PostMapping("web/config/actuarialConfig/editActuarialProduct")
    @ApiOperation(value = "设计精算--批量添加修改设计精算阶段对应的商品信息", notes = "批量添加修改设计精算阶段对应的商品信息")
    ServerResponse editActuarialProduct(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("actuarialProductStr") String actuarialProductStr,
                                        @RequestParam("userId") String userId);

    @PostMapping("web/config/actuarialConfig/deleteActuarialProduct")
    @ApiOperation(value = "设计精算--删除对应的设计精算配置商品", notes = "删除对应的设计精算配置商品")
    ServerResponse deleteActuarialProduct(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("id") String id);

    @PostMapping("web/config/actuarialConfig/getActuarialGoodsList")
    @ApiOperation(value = "设计精算--查询对应设计精算的货品列表", notes = "查询对应设计精算的货品列表")
    ServerResponse getActuarialGoodsList(@RequestParam("request") HttpServletRequest request);

    @PostMapping("web/config/actuarialConfig/getActuarialProductListByGoodsId")
    @ApiOperation(value = "设计精算--查询对应设计精算的货品下的商品列表", notes = "查询对应设计精算的货品下的商品列表")
    ServerResponse getActuarialProductListByGoodsId(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("goodsId") String goodsId);



    /**
     *
     * @param request
     * @param configDetailArr(问题下的选项值列表）
     * @param configId  模拟配置问题 ID
     * @param configName 模拟配置问题 名称
     * @param configType 模拟配置问题模板类型（A图片和文字，B仅图片，C仅文字）
     * @return
     */
    @PostMapping("web/config/actuarialConfig/editSimulateionTemplateConfig")
    @ApiOperation(value = "模拟花费--模板问题选项配置", notes = "模拟花费--模板问题选项配置")
    ServerResponse editSimulateionTemplateConfig(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("configDetailArr") String configDetailArr,
                                        @RequestParam("configId") String configId,
                                        @RequestParam("configName") String configName,
                                        @RequestParam("configType") String configType);

    @PostMapping("web/config/actuarialConfig/querySimulateionTemplateConfig")
    @ApiOperation(value = "模拟花费--查询标题列表", notes = "查询标题列表")
    ServerResponse querySimulateionTemplateConfig(@RequestParam("request") HttpServletRequest request);

    @PostMapping("web/config/actuarialConfig/querySimulateionTemplateConfigById")
    @ApiOperation(value = "模拟花费--单个标题信息", notes = "单个标题信息")
    ServerResponse querySimulateionTemplateConfigById(@RequestParam("request") HttpServletRequest request,
                                                   @RequestParam("simulationTemplateId") String simulationTemplateId);

    @PostMapping("web/config/actuarialConfig/querySimulateionDetailInfoById")
    @ApiOperation(value = "模拟花费--查询单个标题对应的详情信息", notes = "查询单个标题对应的详情信息")
    ServerResponse querySimulateionDetailInfoById(@RequestParam("request") HttpServletRequest request,
                                                      @RequestParam("simulationDetailId") String simulationDetailId);

    @PostMapping("web/config/actuarialConfig/deleteSimulateDetailInfoById")
    @ApiOperation(value = "模拟花费--根据标题ID删除标题及对应的选项值", notes = "根据标题ID删除标题及对应的选项值")
    ServerResponse deleteSimulateDetailInfoById(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("simulationTemplateId") String simulationTemplateId);
}
