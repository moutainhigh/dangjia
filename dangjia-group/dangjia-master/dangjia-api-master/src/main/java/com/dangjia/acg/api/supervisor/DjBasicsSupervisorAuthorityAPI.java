package com.dangjia.acg.api.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * 督导权限配置接口
 * author:chenyufeng
 * time:2019.12.11
 */

@Api(description = "督导权限配置接口")
@FeignClient("dangjia-service-master")
public interface DjBasicsSupervisorAuthorityAPI {


    @PostMapping("web/supervisor/queryDvResponsibility")
    @ApiOperation(value = "督导-查看责任划分", notes = "查看责任划分")
    ServerResponse queryDvResponsibility(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("houseId") String houseId, @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("web/supervisor/querySupervisorHostDetailList")
    @ApiOperation(value = "督导-工地详情", notes = "督导-工地详情")
    ServerResponse querySupervisorHostDetailList(@RequestParam("request") HttpServletRequest request,
                                                 @RequestParam("houseId") String houseId);

    @PostMapping("web/supervisor/queryMaintenanceHostList")
    @ApiOperation(value = "督导-（维修)工地列表", notes = "督导-（维修)工地列表")
    ServerResponse queryMaintenanceHostList(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("pageDTO") PageDTO pageDTO,
                                            @RequestParam("userToken") String userToken,
                                            @RequestParam("keyWord") String keyWord);

    @PostMapping("web/supervisor/queryMtHostListDetail")
    @ApiOperation(value = "督导-（维修）工地详情", notes = "督导-（维修）工地详情")
    ServerResponse queryMtHostListDetail(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("userToken") String userToken,
                                         @RequestParam("houseId") String houseId);


    @PostMapping("web/supervisor/queryApplicationInfo")
    @ApiOperation(value = "督导-（维修）申请信息", notes = "督导-（维修）申请信息")
    ServerResponse queryApplicationInfo(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("houseId") String houseId, @RequestParam("pageDTO") PageDTO pageDTO);


}
