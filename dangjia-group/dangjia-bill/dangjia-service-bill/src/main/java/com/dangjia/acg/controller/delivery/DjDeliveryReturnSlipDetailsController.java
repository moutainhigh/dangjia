package com.dangjia.acg.controller.delivery;

import com.dangjia.acg.api.delivery.DjDeliveryReturnSlipDetailsAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.delivery.DjDeliveryReturnSlipDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 14/10/2019
 * Time: 下午 3:56
 */
@RestController
public class DjDeliveryReturnSlipDetailsController implements DjDeliveryReturnSlipDetailsAPI {

    @Autowired
    private DjDeliveryReturnSlipDetailsService djDeliveryReturnSlipDetailsService;


    @Override
    @ApiMethod
    public ServerResponse queryTaskDetails(HttpServletRequest request, String id) {
        return djDeliveryReturnSlipDetailsService.queryTaskDetails(id);
    }
}
