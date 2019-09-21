package com.dangjia.acg.api.product;

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
 * Date: 2019/9/20
 * Time: 16:31
 */
@Api(description = "精算配置默认展示接口")
@FeignClient("dangjia-service-goods")
public interface DjBasicsActuarialConfigurationAPI {

    @PostMapping("web/product/djBasicsActuarialConfiguration/addConfiguration")
    @ApiOperation(value = "添加配置", notes = "添加配置")
    ServerResponse addConfiguration(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("jsonStr") String jsonStr);


    @PostMapping("web/product/djBasicsActuarialConfiguration/queryConfiguration")
    @ApiOperation(value = "查询配置", notes = "查询配置")
    ServerResponse queryConfiguration(@RequestParam("request") HttpServletRequest request);

    @PostMapping("web/product/djBasicsActuarialConfiguration/querySingleConfiguration")
    @ApiOperation(value = "查询单个配置", notes = "查询单个配置")
    ServerResponse querySingleConfiguration(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("phaseId") String phaseId);
}
