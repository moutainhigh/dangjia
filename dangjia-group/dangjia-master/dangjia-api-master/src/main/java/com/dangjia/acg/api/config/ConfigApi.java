package com.dangjia.acg.api.config;

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
 * Date: 2019/6/13
 * Time: 14:31
 */
@FeignClient("dangjia-service-master")
@Api(value = "Config配置接口", description = "Config配置接口")
public interface ConfigApi {

    @PostMapping("/config/editDistance")
    @ApiOperation(value = "施工现场距离配置", notes = "施工现场距离配置")
    ServerResponse editDistance(@RequestParam("distance") double distance,@RequestParam("radius") double radius);

    @PostMapping("/config/selectDistance")
    @ApiOperation(value = "施工现场距离展示", notes = "施工现场距离展示")
    ServerResponse selectDistance();
}
