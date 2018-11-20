package com.dangjia.acg.api.web.house;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/10 0010
 * Time: 10:11
 */
@FeignClient("dangjia-service-master")
@Api(value = "设计风格接口", description = "设计风格接口")
public interface WebHouseStyleTypeAPI {

    @PostMapping("web/houseStyle/getStyleList")
    @ApiOperation(value = "设计风格列表", notes = "设计风格列表")
    ServerResponse getList(@RequestParam("request") HttpServletRequest request);
}
