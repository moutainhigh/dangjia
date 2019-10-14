package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.DjDeliveryReturnSlipAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.delivery.DjDeliveryReturnSlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 上午 10:37
 */
@RestController
public class DjDeliveryReturnSlipController implements DjDeliveryReturnSlipAPI {

    @Autowired
    private DjDeliveryReturnSlipService djDeliveryReturnSlipService;

    @Override
    @ApiMethod
    public ServerResponse querySupplyTaskList(HttpServletRequest request, PageDTO pageDTO, String supId, String searchKey, String invoiceStatus) {
        return djDeliveryReturnSlipService.querySupplyTaskList(pageDTO, supId, searchKey, invoiceStatus);
    }

    @Override
    @ApiMethod
    public ServerResponse setDeliveryTask(HttpServletRequest request, String id, String invoiceStatus) {
        return djDeliveryReturnSlipService.setDeliveryTask(id,invoiceStatus);
    }
}
