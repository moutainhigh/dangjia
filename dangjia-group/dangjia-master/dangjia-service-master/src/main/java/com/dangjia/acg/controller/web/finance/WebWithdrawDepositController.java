package com.dangjia.acg.controller.web.finance;

import com.dangjia.acg.api.web.finance.WebWithdrawDepositAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.service.finance.WebWithdrawDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: ysl
 * Date: 2019/1/24 0004
 * Time: 17:49
 */
@RestController
public class WebWithdrawDepositController implements WebWithdrawDepositAPI {

    @Autowired
    private WebWithdrawDepositService webWithdrawDepositService;

    @Override
    @ApiMethod
    public ServerResponse getAllWithdraw(HttpServletRequest request, PageDTO pageDTO, String searchKey, Integer state, String beginDate, String endDate) {
        String cityId = request.getParameter(Constants.CITY_ID);
        return webWithdrawDepositService.getAllWithdraw(pageDTO,  cityId,searchKey, state, beginDate, endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse setWithdraw(HttpServletRequest request, WithdrawDeposit withdrawDeposit) {
        return webWithdrawDepositService.setWithdraw(withdrawDeposit);
    }
}
