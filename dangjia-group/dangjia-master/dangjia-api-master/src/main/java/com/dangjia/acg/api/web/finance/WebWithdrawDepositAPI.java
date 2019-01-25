package com.dangjia.acg.api.web.finance;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:52
 */
@FeignClient("dangjia-service-master")
@Api(value = "工匠提现", description = "工匠提现")
public interface WebWithdrawDepositAPI {

    /**
     * 查询所有有提现申请
     */
    @PostMapping("web/finance/withdraw/getAllWithdraw")
    @ApiOperation(value = "查询所有提现申请", notes = "查询所有有提现申请")
    ServerResponse getAllWithdraw(@RequestParam("request") HttpServletRequest request, @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 修改提现信息状态
     */
    @PostMapping("web/finance/withdraw/updateWithdraw")
    @ApiOperation(value = "提醒申请驳回，同意", notes = "提醒申请驳回，同意")
    ServerResponse updateWithdraw(@RequestParam("request") HttpServletRequest request, @RequestParam("workerId") String workerId);

}
