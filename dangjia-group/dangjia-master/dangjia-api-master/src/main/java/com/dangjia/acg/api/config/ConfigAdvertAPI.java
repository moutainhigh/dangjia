package com.dangjia.acg.api.config;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigAdvert;
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
@Api(value = "广告接口", description = "广告接口")
public interface ConfigAdvertAPI {
    /**
     * 获取所有广告
     *
     * @param configAdvert
     * @return
     */
    @PostMapping("/config/adverts/list")
    @ApiOperation(value = "获取所有广告", notes = "获取所有广告")
    ServerResponse getConfigAdverts(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("configAdvert") ConfigAdvert configAdvert);

    /**
     * 删除广告
     *
     * @param id
     * @return
     */
    @PostMapping("/config/adverts/del")
    @ApiOperation(value = "删除广告", notes = "删除广告")
    ServerResponse delConfigAdvert(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("id") String id);

    /**
     * 修改广告
     *
     * @param configAdvert
     * @return
     */
    @PostMapping("/config/adverts/edit")
    @ApiOperation(value = "修改广告", notes = "修改广告")
    ServerResponse editConfigAdvert(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("configAdvert") ConfigAdvert configAdvert);

    /**
     * 新增广告
     *
     * @param configAdvert
     * @return
     */
    @PostMapping("/config/adverts/add")
    @ApiOperation(value = "新增广告", notes = "新增广告")
    ServerResponse addConfigAdvert(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("configAdvert") ConfigAdvert configAdvert);

}
