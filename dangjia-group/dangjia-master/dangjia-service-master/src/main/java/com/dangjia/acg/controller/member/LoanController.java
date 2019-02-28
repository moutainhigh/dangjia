package com.dangjia.acg.controller.member;

import com.dangjia.acg.api.member.LoanAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.member.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 贷款接口Controller
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/27 4:06 PM
 */
@RestController
public class LoanController implements LoanAPI {
    @Autowired
    private LoanService loanService;

    @Override
    @ApiMethod
    public ServerResponse addLoan(HttpServletRequest request, String userToken, String name, String bankName) {
        return loanService.addLoan(userToken, name, bankName);
    }

    @Override
    @ApiMethod
    public ServerResponse getLoanList(HttpServletRequest request, PageDTO pageDTO, Integer state, String searchKey) {
        return loanService.getLoanList(request, pageDTO, state, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse getLoanFlowList(HttpServletRequest request, PageDTO pageDTO, String loanId) {
        return loanService.getLoanFlowList(request, pageDTO, loanId);
    }

    @Override
    @ApiMethod
    public ServerResponse updataLoan(HttpServletRequest request, String userId, String loanId, Integer state, String stateDescribe) {
        return loanService.updataLoan(request, userId, loanId, state, stateDescribe);
    }
}
