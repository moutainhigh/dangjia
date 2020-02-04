package com.dangjia.acg.controller.app.member;

import com.dangjia.acg.api.app.member.WalletAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.member.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/12/18 0018
 * Time: 16:54
 */
@RestController
public class WalletController implements WalletAPI {
    @Autowired
    private WalletService walletService;

    /**
     * 完成验证提现
     */
    @Override
    @ApiMethod
    public ServerResponse checkFinish(String userToken, String paycode, Double money, String workerBankCardId, Integer roleType) {
        return walletService.checkFinish(userToken, paycode, money, workerBankCardId, roleType);
    }

    /**
     * 提现验证码
     */
    @Override
    @ApiMethod
    public ServerResponse getPaycode(String userToken) {
        return walletService.getPaycode(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse verificationAmount(String userToken, Double money) {
        return walletService.verificationAmount(userToken, money);
    }

    /**
     * 获取提现信息
     */
    @Override
    @ApiMethod
    public ServerResponse getWithdraw(String userToken) {
        return walletService.getWithdraw(userToken);
    }

    /**
     * 流水详情
     */
    @Override
    @ApiMethod
    public ServerResponse getExtractDetail(String userToken, String workerDetailId) {
        return walletService.getExtractDetail(workerDetailId);
    }

    /**
     * 资产流水
     */
    @Override
    @ApiMethod
    public ServerResponse workerDetail(String userToken,String time, String timeMark,Integer type,Integer state,  PageDTO pageDTO) {
        return walletService.workerDetail(userToken,time, timeMark,type, state,  pageDTO);
    }

    /**
     * 钱包信息, 查询余额
     */
    @Override
    @ApiMethod
    public ServerResponse walletInformation(String userToken) {
        return walletService.walletInformation(userToken);
    }

    /**
     * 指定某年的收入趋势
     */
    @Override
    @ApiMethod
    public ServerResponse getIncomeTrend(String userToken, String time){
        return walletService.getIncomeTrend(userToken,time);
    }
}
