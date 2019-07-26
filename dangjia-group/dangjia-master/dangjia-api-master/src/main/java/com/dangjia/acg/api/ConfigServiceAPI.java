package com.dangjia.acg.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by QiYuXiang on 2017/8/3.
 */
@FeignClient("dangjia-service-master")
@Api(value = "系统配置的接口",description = "系统配置的接口")
public interface ConfigServiceAPI {

    /****
     * 获取系统参数值
     * @param name
     * @param appType
     * @return
     */
    @RequestMapping(value = "getValue", method = RequestMethod.POST)
    @ApiOperation(value = "获取系统参数值", notes = "获取系统参数值")
    byte[] getValue(@RequestParam("name") String name, @RequestParam("appType") Integer appType);

    /*****
     * 更新系统参数值
     * @param name 参数名
     * @param appType 平台
     * @param value 参数值
     */
    @RequestMapping(value = "updateValue", method = RequestMethod.POST)
    @ApiOperation(value = "更新系统参数值", notes = "更新系统参数值")
    void updateValue(@RequestParam("name") String name, @RequestParam("appType") Integer appType, @RequestParam("value") String value);


}
