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

    @PostMapping("web/supervisor/queryDvResponsibility")
    @ApiOperation(value = "督导-查看责任划分", notes = "查看责任划分")
    ServerResponse queryDvResponsibility(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("houseId") String houseId, @RequestParam("pageDTO") PageDTO pageDTO);

    @PostMapping("web/supervisor/querySupervisorHostList")
    @ApiOperation(value = "督导-工地列表", notes = "督导-工地列表")
    ServerResponse querySupervisorHostList(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("sortNum") String sortNum,
                                           @RequestParam("pageDTO") PageDTO pageDTO,
                                           @RequestParam("userToken") String userToken,
                                           @RequestParam("keyWord") String keyWord);

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


    @PostMapping("web/supervisor/queryInvestigationDetail")
    @ApiOperation(value = "督导-（维修）勘察详情", notes = "督导-（维修）勘察详情")
    ServerResponse queryInvestigationDetail(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("houseId") String houseId,@RequestParam("pageDTO") PageDTO pageDTO);


    @PostMapping("web/supervisor/queryApplicationInfo")
    @ApiOperation(value = "督导-（维修）申请信息", notes = "督导-（维修）申请信息")
    ServerResponse queryApplicationInfo(@RequestParam("request") HttpServletRequest request,
                                        @RequestParam("houseId") String houseId,@RequestParam("pageDTO") PageDTO pageDTO);


}
