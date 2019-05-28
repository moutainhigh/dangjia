package com.dangjia.acg.controller.app.repair;

import com.dangjia.acg.api.app.repair.MendDeliverAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.MendDeliverServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/24
 * Time: 16:15
 */
@RestController
public class MendDeliverController implements MendDeliverAPI {

    @Autowired
    private MendDeliverServices mendDeliverServices;


    @Override
    @ApiMethod
    public ServerResponse mendDeliverList(String supplierId) {
        return mendDeliverServices.mendDeliverList(supplierId);
    }

    @Override
    @ApiMethod
    public ServerResponse mendDeliverDetail(String mendDeliverId) {
        return mendDeliverServices.mendDeliverDetail(mendDeliverId);
    }
}
