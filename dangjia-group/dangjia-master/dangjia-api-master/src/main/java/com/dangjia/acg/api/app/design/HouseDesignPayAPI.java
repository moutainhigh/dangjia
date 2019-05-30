package com.dangjia.acg.api.app.design;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient("dangjia-service-master")
@Api(value = "设计图修改支付接口", description = "设计图修改支付接口")
public interface HouseDesignPayAPI {


    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param houseId   必选 string houseId
     * @return {"res":1000,"msg":{"resultCode":1000,"resultObj":{resultCode=1009时，返回参数说明},"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 业主发起修改设计图
     * @description 业主发起修改设计图
     * @method POST
     * @url master/app/design/modifyDesign
     * @return_param message string 头部提示信息
     * @return_param butName string 协议名称
     * @return_param butUrl string 协议地址
     * @return_param moneyMessage string 金额描叙
     * @return_param businessOrderNumber string 订单ID
     * @return_param type int 支付任务type
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 15
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:04 PM
     */
    @PostMapping("app/design/modifyDesign")
    @ApiOperation(value = "业主发起修改设计图", notes = "业主发起修改设计图")
    ServerResponse modifyDesign(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("userToken") String userToken,
                                @RequestParam("houseId") String houseId);

    /**
     * showdoc
     *
     * @param userToken 必选 string userToken
     * @param houseId   必选 string houseId
     * @param type      必选 int 0:不通过,1:通过
     * @return {"res":1000,"msg":{"resultCode":1000,"resultObj":{resultCode=1009时，返回参数说明},"resultMsg":"成功"} }
     * @catalog 当家接口文档/设计模块
     * @title 业主发起修改设计图
     * @description 业主发起修改设计图
     * @method POST
     * @url master/app/design/confirmDesign
     * @return_param message string 头部提示信息
     * @return_param butName string 协议名称
     * @return_param butUrl string 协议地址
     * @return_param moneyMessage string 金额描叙
     * @return_param businessOrderNumber string 订单ID
     * @return_param type int 支付任务type
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 16
     * @Author: Ruking 18075121944
     * @Date: 2019/5/7 2:04 PM
     */
    @PostMapping("app/design/confirmDesign")
    @ApiOperation(value = "修改后确认设计图", notes = "修改后确认设计图")
    ServerResponse confirmDesign(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userToken") String userToken,
                                 @RequestParam("houseId") String houseId,
                                 @RequestParam("type") int type);

}
