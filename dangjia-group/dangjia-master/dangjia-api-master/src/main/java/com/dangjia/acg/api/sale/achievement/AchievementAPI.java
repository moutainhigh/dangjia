package com.dangjia.acg.api.sale.achievement;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * 业绩模块 API
 * author: ljl
 * Date: 2019/7/27
 * Time: 9:59
 */
@FeignClient("dangjia-service-master")
@Api(value = "销售端业绩接口", description = "销售端业绩接口")
public interface AchievementAPI {

    /**
     * showdoc
     * @catalog TODO 当家接口文档/设计模块
     * @title TODO
     * @description TODO
     * @method POST
     * @url TODO master/
     * @param request 必选/可选 string TODO
     * @param storeId 必选/可选 string TODO
     * @param userId 必选/可选 string TODO
     * @param time 必选/可选 string TODO
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: ljl 18075121944
     * @Date: 2019/7/31 0031 18:05
     */
    @PostMapping(value = "sale/achievement/queryLeaderAchievementData")
    @ApiOperation(value = "客户查询业绩", notes = "客户查询业绩")
    ServerResponse queryLeaderAchievementData(@RequestParam("request") HttpServletRequest request,
                                              @RequestParam("userToken")String userToken,
                                              @RequestParam("storeId") String storeId,
                                              @RequestParam("userId") String userId,
                                              @RequestParam("time") Date time);


    /**
     * showdoc
     * @catalog TODO 当家接口文档/设计模块
     * @title TODO
     * @description TODO
     * @method POST
     * @url TODO master/
     * @param request 必选/可选 string TODO
     * @param visitState 必选/可选 string TODO
     * @param userId 必选/可选 string TODO
     * @param time 必选/可选 string TODO
     * @return {"res":1000,"msg":{"resultObj":{返回参数说明},"resultCode":1000,"resultMsg":"成功"} }
     * @return_param groupid int 用户组id
     * @return_param name string 用户昵称
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 99
     * @Author: ljl 18075121944
     * @Date: 2019/7/31 0031 18:05
     */
    @PostMapping(value = "sale/achievement/queryUserAchievementData")
    @ApiOperation(value = "查询员工业绩", notes = "查询员工业绩")
    ServerResponse queryUserAchievementData(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("visitState") Integer visitState,
                                            @RequestParam("userId") String userId,
                                            @RequestParam("time") Date time);
}
