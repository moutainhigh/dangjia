package com.dangjia.acg.api.member;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 贷款接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/27 11:33 AM
 */
@FeignClient("dangjia-service-master")
@Api(value = "贷款接口", description = "贷款接口")
public interface LoanAPI {


    @PostMapping("/member/loan/add")
    @ApiOperation(value = "添加贷款需求", notes = "添加贷款需求")
    ServerResponse addLoan(@RequestParam("request") HttpServletRequest request,
                           @RequestParam("userToken") String userToken,
                           @RequestParam("name") String name,
                           @RequestParam("bankName") String bankName);

    @PostMapping("/member/loan/list")
    @ApiOperation(value = "查询贷款需求列表", notes = "查询贷款需求列表")
    ServerResponse getLoanList(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("pageDTO") PageDTO pageDTO,
                               @RequestParam("state") Integer state,
                               @RequestParam("searchKey") String searchKey);

    @PostMapping("/member/loanFlow/list")
    @ApiOperation(value = "查询贷款需求列表", notes = "查询贷款需求列表")
    ServerResponse getLoanFlowList(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("pageDTO") PageDTO pageDTO,
                                   @RequestParam("loanId") String loanId);


    @PostMapping("/member/loan/updata")
    @ApiOperation(value = "修改贷款需求状态", notes = "修改贷款需求状态")
    ServerResponse updataLoan(@RequestParam("request") HttpServletRequest request,
                              @RequestParam("userId") String userId,
                              @RequestParam("loanId") String loanId,
                              @RequestParam("state") Integer state,
                              @RequestParam("stateDescribe") String stateDescribe);
}
