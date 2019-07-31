package com.dangjia.acg.api.sale.rob;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * 抢单模块 API
 * author: ljl
 * Date: 2019/7/27
 * Time: 9:59
 */
@FeignClient("dangjia-service-master")
@Api(value = "抢单模块", description = "抢单模块")
public interface RobAPI {

    @PostMapping(value = "sale/rob/queryRobSingledata")
    @ApiOperation(value = "抢单列表查询", notes = "抢单列表查询")
    ServerResponse queryRobSingledata(HttpServletRequest request, String userId,String storeId);
}
