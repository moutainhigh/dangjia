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

@Api(description = "督导权限配置接口")
@FeignClient("dangjia-service-master")
public interface DjBasicsSupervisorAuthorityAPI {

    @PostMapping("web/supervisor/delAuthority")
    @ApiOperation(value = "删除已选", notes = "删除已选")
    ServerResponse delAuthority(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("id") String id);

    @PostMapping("web/supervisor/searchAuthority")
    @ApiOperation(value = "搜索已选", notes = "搜索已选")
    ServerResponse searchAuthority(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("visitState") String visitState,
                                   @RequestParam("keyWord") String keyWord,
                                   @RequestParam("pageDTO")  PageDTO pageDTO);

    @PostMapping("web/supervisor/addAuthority")
    @ApiOperation(value = "增加已选", notes = "增加已选")
    ServerResponse addAuthority(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("DjBasicsSupervisorAuthority") DjBasicsSupervisorAuthority djBasicsSupervisorAuthority);

    @PostMapping("web/supervisor/addAllAuthority")
    @ApiOperation(value = "批量增加已选", notes = "批量增加已选")
    ServerResponse addAllAuthority(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("strAuthority") String strAuthority,   @RequestParam("operateId") String operateId);


    @PostMapping("web/supervisor/queryApplicationInfo")
    @ApiOperation(value = "查看申请信息", notes = "查看申请信息")
    ServerResponse queryApplicationInfo(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("houseId") String houseId,@RequestParam("pageDTO") PageDTO pageDTO);


    @PostMapping("web/supervisor/queryDvResponsibility")
    @ApiOperation(value = "查看责任划分", notes = "查看责任划分")
    ServerResponse queryDvResponsibility(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("houseId") String houseId);


    @PostMapping("web/supervisor/queryAcceptanceTrend")
    @ApiOperation(value = "查看验收动态", notes = "查看验收动态")
    ServerResponse queryAcceptanceTrend(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("houseId") String houseId);
    //工地列表

    //工地详情

    @PostMapping("web/supervisor/queryMtHostList")
    @ApiOperation(value = "（维修)工地列表", notes = "（维修)工地列表")
    ServerResponse queryMaintenanceHostList(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("houseId") String houseId);

    @PostMapping("web/supervisor/queryMtHostListDetail")
    @ApiOperation(value = "（维保）工地详情", notes = "（维保）工地详情")
    ServerResponse queryMtHostListDetail(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("houseId") String houseId);


}
