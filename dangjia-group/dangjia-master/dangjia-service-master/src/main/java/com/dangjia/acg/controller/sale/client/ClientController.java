package com.dangjia.acg.controller.sale.client;

import com.dangjia.acg.api.sale.client.ClientAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.service.sale.client.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/20
 * Time: 9:59
 */
@RestController
public class ClientController implements ClientAPI {
    @Autowired
    private ClientService customerService;

    @Override
    @ApiMethod
    public ServerResponse enterCustomer(HttpServletRequest request, Clue clue ,String userToken) {
        return customerService.enterCustomer(clue,userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse crossDomainOrder(HttpServletRequest request, Clue clue, String userToken, String cityId, String villageId) {
        return customerService.crossDomainOrder(clue,userToken,cityId,villageId);
    }

    @Override
    @ApiMethod
    public ServerResponse updateCustomer(HttpServletRequest request, Clue clue) {
        return customerService.updateCustomer(clue);
    }

    @Override
    @ApiMethod
    public ServerResponse clientPage(HttpServletRequest request, String userToken) {
        return customerService.clientPage(userToken);
    }

    @Override
    @ApiMethod
    public ServerResponse followList(HttpServletRequest request, String userToken, PageDTO pageDTO, String label, String time, Integer stage, String searchKey,String userId) {
        return customerService.followList(userToken,pageDTO,label,time,stage,searchKey,userId);
    }

    @Override
    @ApiMethod
    public ServerResponse ordersCustomer(HttpServletRequest request, String userToken,String visitState , PageDTO pageDTO,String searchKey, String time,Integer type,String userId) {
        return customerService.ordersCustomer(userToken,visitState,pageDTO,searchKey,time,type,userId);
    }
}
