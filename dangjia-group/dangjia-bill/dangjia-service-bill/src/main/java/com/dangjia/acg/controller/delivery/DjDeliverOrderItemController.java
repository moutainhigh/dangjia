package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.DjDeliverOrderItemAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DjDeliverOrderItemController implements DjDeliverOrderItemAPI {

    @Override
    @ApiMethod
    public ServerResponse queryAllDeliverOrderDetail(HttpServletRequest request, PageDTO pageDTO,  String orderId,String userId, String cityId) {
        return null;
    }

}
