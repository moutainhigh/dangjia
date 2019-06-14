package com.dangjia.acg.api.home;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 新版首页模块获取数据接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/14 3:55 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "新版首页模块获取数据接口", description = "新版首页模块获取数据接口")
public interface HomeModularAPI {
    /**
     * showdoc
     *
     * @return {"res":1000,"msg":{"resultObj":[{返回参数说明},{返回参数说明}],"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/首页模块
     * @title 首页获取播报
     * @description 首页获取播报
     * @method POST
     * @url master/home/getBroadcastList
     * @return_param describe string 播报内容
     * @return_param houseId string houseId
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 13
     * @Author: Ruking 18075121944
     * @Date: 2019/6/14 4:27 PM
     */
    @PostMapping("home/getBroadcastList")
    @ApiOperation(value = "首页获取播报", notes = "首页获取播报")
    ServerResponse getBroadcastList(@RequestParam("request") HttpServletRequest request);
}
