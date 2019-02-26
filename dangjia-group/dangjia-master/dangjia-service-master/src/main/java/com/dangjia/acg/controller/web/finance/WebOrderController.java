package com.dangjia.acg.controller.web.finance;

import com.dangjia.acg.api.web.finance.WebOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.finance.WebOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: ysl
 * Date: 2019/1/24 0004
 * Time: 17:49
 */
@RestController
public class WebOrderController implements WebOrderAPI {

    @Autowired
    private WebOrderService webOrderService;

    @Override
    @ApiMethod
    public ServerResponse getAllOrders(HttpServletRequest request, PageDTO pageDTO, String likeMobile, String likeAddress) {
        return webOrderService.getAllOrders(pageDTO, likeMobile, likeAddress);
    }
}
