package com.dangjia.acg.api.config;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigAppLogo;
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
@Api(value = "应用LOGO接口", description = "应用LOGO接口")
public interface ConfigAppLogoAPI {

    @PostMapping("/config/appLogo/get")
    @ApiOperation(value = "获取当前配置logo", notes = "获取当前配置logo")
    ServerResponse getConfigAppLogo(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("configAppLogo") ConfigAppLogo configAppLogo);

    /**
     * 获取所有应用LOGO
     *
     * @param configAppLogo
     * @return
     */
    @PostMapping("/config/appLogo/list")
    @ApiOperation(value = "获取所有应用LOGO", notes = "获取所有应用LOGO")
    ServerResponse getConfigAppLogos(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("configAppLogo") ConfigAppLogo configAppLogo);

    /**
     * 删除应用LOGO
     *
     * @param id
     * @return
     */
    @PostMapping("/config/appLogo/del")
    @ApiOperation(value = "删除应用LOGO", notes = "删除应用LOGO")
    ServerResponse delConfigAppLogo(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("id") String id);

    /**
     * 修改应用LOGO
     *
     * @param configAppLogo
     * @return
     */
    @PostMapping("/config/appLogo/edit")
    @ApiOperation(value = "修改应用LOGO", notes = "修改应用LOGO")
    ServerResponse editConfigAppLogo(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("configAppLogo") ConfigAppLogo configAppLogo);

    /**
     * 新增应用LOGO
     *
     * @param configAppLogo
     * @return
     */
    @PostMapping("/config/appLogo/add")
    @ApiOperation(value = "新增应用LOGO", notes = "新增应用LOGO")
    ServerResponse addConfigAppLogo(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("configAppLogo") ConfigAppLogo configAppLogo);

}
