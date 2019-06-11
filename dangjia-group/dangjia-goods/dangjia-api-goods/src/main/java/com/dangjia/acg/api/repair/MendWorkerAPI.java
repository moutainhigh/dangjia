package com.dangjia.acg.api.repair;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 17:06
 */
@Api(description = "补人工管理")
@FeignClient("dangjia-service-goods")
public interface MendWorkerAPI {

    @PostMapping("/repair/mendWorker/repairBudgetWorker")
    @ApiOperation(value = "补人工查询", notes = "补人工查询")
    ServerResponse repairBudgetWorker(@RequestParam("request") HttpServletRequest request,
                                      @RequestParam("type") int type,
                                      @RequestParam("workerTypeId") String workerTypeId,
                                      @RequestParam("houseId") String houseId,
                                      @RequestParam("name") String name,
                                      @RequestParam("pageDTO") PageDTO pageDTO);

}
