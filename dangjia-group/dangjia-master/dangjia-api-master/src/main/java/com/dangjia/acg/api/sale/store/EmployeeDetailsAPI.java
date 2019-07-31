package com.dangjia.acg.api.sale.store;

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
 * Date: 2019/7/22
 * Time: 14:23
 */
@FeignClient("dangjia-service-master")
@Api(value = "销售员工详情接口", description = "销售员工详情接口")
public interface EmployeeDetailsAPI {

    /**
     * showdoc
     *
     * @param userId 必选 string 员工id
     * @param time   必选 string 目标月份yyyy-MM
     * @param target 必选 Integer 目标数量
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/店长门店管理
     * @title 制定员工月目标
     * @description 制定员工月目标
     * @method POST
     * @url master/sale/store/monthlyTarget
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 7
     * @Author: Ruking 18075121944
     * @Date: 2019/7/29 5:21 PM
     */
    @PostMapping(value = "sale/store/monthlyTarget")
    @ApiOperation(value = "制定员工月目标", notes = "制定员工月目标")
    ServerResponse setMonthlyTarget(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("userId") String userId,
                                    @RequestParam("time") String time,
                                    @RequestParam("target") Integer target);

    /**
     * showdoc
     *
     * @param userId 必选 string 员工id
     * @param buildingId   可选 string 楼栋id字符串
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 当家接口文档/销售模块/店长门店管理
     * @title 制定员工月目标
     * @description 制定员工月目标
     * @method POST
     * @url master/sale/store/monthlyTarget
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 7
     * @Author: wukang 18075121944
     * @Date: 2019/7/29 5:21 PM
     */
    @PostMapping(value = "sale/store/setSalesRange")
    @ApiOperation(value = "配置员工销售范围", notes = "配置员工销售范围")
    ServerResponse setSalesRange(@RequestParam("request") HttpServletRequest request,
                                 @RequestParam("userId") String userId,
                                 @RequestParam("buildingId") String buildingId);



    @PostMapping(value = "sale/store/delMonthlyTarget")
    @ApiOperation(value = "删除员工月目标", notes = "删除员工月目标")
    ServerResponse delMonthlyTarget(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("monthlyTargetId") String monthlyTargetId);



}
