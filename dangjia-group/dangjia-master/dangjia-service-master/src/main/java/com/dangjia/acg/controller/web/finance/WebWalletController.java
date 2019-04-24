package com.dangjia.acg.controller.web.finance;

import com.dangjia.acg.api.web.finance.WebWalletAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.worker.WorkerDetail;
import com.dangjia.acg.service.finance.WebWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: ysl
 * Date: 2019/1/24 0004
 * Time: 17:49
 */
@RestController
public class WebWalletController implements WebWalletAPI {

    @Autowired
    private WebWalletService webWalletService;

    /**
     * --         0每日完工  1阶段完工，
     * --         2整体完工  3巡查, 4验收,
     * --         8补人工, 9退人工, 10奖 11罚
     * @param houseId
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseWallet(String houseId,  String  userToken){
        return webWalletService.getHouseWallet(houseId,userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse getAllWallet(HttpServletRequest request, PageDTO pageDTO, String workerId, String houseId, String likeMobile, String likeAddress) {
        return webWalletService.getAllWallet(pageDTO, workerId, houseId, likeMobile, likeAddress);
    }

    @Override
    @ApiMethod
    public ServerResponse addWallet(HttpServletRequest request, WorkerDetail workerDetail) {
        return webWalletService.addWallet(workerDetail);
    }
}
