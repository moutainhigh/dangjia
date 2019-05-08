package com.dangjia.acg.api.app.repair;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2019/1/18 0018
 * Time: 14:42
 */
@FeignClient("dangjia-service-master")
@Api(value = "审核流程", description = "审核流程")
public interface MendOrderCheckAPI {

    @PostMapping(value = "app/repair/mendOrderCheck/auditSituation")
    @ApiOperation(value = "查询审核情况", notes = "查询审核情况")
    ServerResponse auditSituation(@RequestParam("mendOrderId") String mendOrderId);

    @PostMapping(value = "app/repair/mendOrderCheck/checkMendOrder")
    @ApiOperation(value = "审核补退单", notes = "审核补退单")
    ServerResponse checkMendOrder(@RequestParam("userToken") String userToken,
                                  @RequestParam("mendOrderId") String mendOrderId,
                                  @RequestParam("roleType") String roleType,
                                  @RequestParam("state") Integer state,
                                  @RequestParam("productArr") String productArr
    );

    @PostMapping(value = "app/repair/mendOrderCheck/confirmMendOrder")
    @ApiOperation(value = "大管家确认退货单", notes = "大管家确认退货单")
    ServerResponse confirmMendOrder(@RequestParam("userToken") String userToken,
                                  @RequestParam("mendOrderId") String mendOrderId,
                                  @RequestParam("productArr") String productArr
    );
}
