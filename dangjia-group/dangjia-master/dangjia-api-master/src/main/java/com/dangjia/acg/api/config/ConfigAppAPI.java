package com.dangjia.acg.api.config;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigApp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/11/07
 * Time: 16:16
 */
@FeignClient("dangjia-service-master")
@Api(value = "版本应用接口", description = "版本应用接口")
public interface ConfigAppAPI {

    @PostMapping("/config/app/check")
    @ApiOperation(value = "版本检测", notes = "版本检测")
    ServerResponse checkConfigApp(HttpServletRequest request, ConfigApp configApp);
    /**
     * 获取所有版本应用
     * @param configApp
     * @return
     */
    @PostMapping("/config/app/list")
    @ApiOperation(value = "获取所有版本应用", notes = "获取所有版本应用")
    ServerResponse getConfigApps(@RequestParam("request") HttpServletRequest request, @RequestParam("configApp") ConfigApp configApp);
    /**
     * 删除版本应用
     * @param id
     * @return
     */
    @PostMapping("/config/app/del")
    @ApiOperation(value = "删除版本应用", notes = "删除版本应用")
    ServerResponse delConfigApp(@RequestParam("request") HttpServletRequest request, @RequestParam("id") String id) ;

    /**
     * 修改版本应用
     * @param configApp
     * @return
     */
    @PostMapping("/config/app/edit")
    @ApiOperation(value = "修改版本应用", notes = "修改版本应用")
    ServerResponse editConfigApp(@RequestParam("request") HttpServletRequest request, @RequestParam("configApp") ConfigApp configApp) ;
    /**
     * 新增版本应用
     * @param configApp
     * @return
     */
    @PostMapping("/config/app/add")
    @ApiOperation(value = "新增版本应用", notes = "新增版本应用")
    ServerResponse addConfigApp(@RequestParam("request") HttpServletRequest request, @RequestParam("configApp") ConfigApp configApp);

}
