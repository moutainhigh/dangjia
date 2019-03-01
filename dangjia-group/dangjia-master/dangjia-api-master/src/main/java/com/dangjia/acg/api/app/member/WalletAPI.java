package com.dangjia.acg.api.app.member;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 16:52
 */
@FeignClient("dangjia-service-master")
@Api(value = "个人钱包", description = "个人钱包")
public interface WalletAPI {

    /**
     * 完成验证提现
     */
    @PostMapping("app/member/wallet/checkFinish")
    @ApiOperation(value = "完成验证提现", notes = "完成验证提现")
    ServerResponse checkFinish(@RequestParam("userToken") String userToken,
                               @RequestParam("paycode") Integer paycode,
                               @RequestParam("money") Double money,
                               @RequestParam("workerBankCardId") String workerBankCardId,
                               @RequestParam("roleType") Integer roleType);

    /**
     * 提现验证码
     */
    @PostMapping("app/member/wallet/getPaycode")
    @ApiOperation(value = "提现验证码", notes = "提现验证码")
    ServerResponse getPaycode(@RequestParam("userToken") String userToken);

    /**
     * 获取提现信息
     */
    @PostMapping("app/member/wallet/getWithdraw")
    @ApiOperation(value = "获取提现信息", notes = "获取提现信息")
    ServerResponse getWithdraw(@RequestParam("userToken") String userToken);

    /**
     * 流水详情
     */
    @PostMapping("app/member/wallet/getExtractDetail")
    @ApiOperation(value = "流水详情", notes = "流水详情")
    ServerResponse getExtractDetail(@RequestParam("userToken") String userToken,
                                    @RequestParam("workerDetailId") String workerDetailId);

    /**
     * 支出 收入
     */
    @PostMapping("app/member/wallet/workerDetail")
    @ApiOperation(value = "支出 收入", notes = "支出 收入")
    ServerResponse workerDetail(@RequestParam("userToken") String userToken,
                                @RequestParam("type") int type,
                                @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 钱包信息, 查询余额
     */
    @PostMapping("app/member/wallet/walletInformation")
    @ApiOperation(value = "钱包信息, 查询余额", notes = "钱包信息, 查询余额")
    ServerResponse walletInformation(@RequestParam("userToken") String userToken);
}
