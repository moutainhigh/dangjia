package com.dangjia.acg.api.supplier;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.supplier.DjRegisterApplication;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:03
 */
@Api(description = "供应商注册管理接口")
@FeignClient("dangjia-service-master")
public interface DjRegisterApplicationAPI {

    @PostMapping("/sup/djRegisterApplication/registerSupAndStorefront")
    @ApiOperation(value = "供应商/店铺注册", notes = "供应商/店铺注册")
    ServerResponse registerSupAndStorefront(@RequestParam("request") HttpServletRequest request,
                                            @RequestParam("djRegisterApplication") DjRegisterApplication djRegisterApplication);


    @PostMapping("/sup/djRegisterApplication/querySupAndStorefront")
    @ApiOperation(value = "根据电话号码查询注册信息", notes = "供应商/店铺注册")
    ServerResponse querySupAndStorefront(@RequestParam("request") HttpServletRequest request,
                                         @RequestParam("mobile") String mobile,
                                         @RequestParam("cityId") String cityId);


    @PostMapping("/sup/register/check")
    @ApiOperation(value = "提交审核注册的供应商或店铺", notes = "提交审核注册的供应商或店铺")
    ServerResponse checkSupAndStorefront(HttpServletRequest request, String registerId, Integer isAdopt, String departmentId, String jobId,String failReason);

    @PostMapping("/sup/register/getRegisterList")
    @ApiOperation(value = "查询已申请的供应商列表", notes = "查询已申请的供应商列表")
    ServerResponse<PageInfo> getRegisterList(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("pageDTO") PageDTO pageDTO,
                                             @RequestParam("applicationStatus") String applicationStatus,
                                             @RequestParam("searchKey")  String searchKey);
    @PostMapping("/sup/register/getRegisterInfoById")
    @ApiOperation(value = "查询申请ID查询对应的申请信息", notes = "查询申请ID查询对应的申请信息")
    ServerResponse getRegisterInfoById(@RequestParam("request") HttpServletRequest request,
                                             @RequestParam("id") String id);

}
