package com.dangjia.acg.api.home;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description 新版首页接口（1.4.0）
 * @Author: qiyuxiang
 * @Date: 2019/6/19 3:18 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "新版业主施工接口", description = "新版业主施工接口")
public interface MyHomeAPI {

    /**
     * @param userToken 必选 string 操作人ID
     * @param cityId   必选 string 模版名称
     */
    @PostMapping("app/home/my")
    @ApiOperation(value = "业主端，我的装修界面", notes = "业主端，我的装修界面")
    ServerResponse getMyHouse(@RequestParam("userToken") String userToken, @RequestParam("cityId")String cityId) ;


}
