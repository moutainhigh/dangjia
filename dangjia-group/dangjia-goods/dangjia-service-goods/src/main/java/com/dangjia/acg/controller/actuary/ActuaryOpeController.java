package com.dangjia.acg.controller.actuary;

import com.dangjia.acg.api.actuary.ActuaryOpeAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.actuary.ActuaryOpeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * author: Ronalcheng
 * Date: 2019/2/26 0026
 * Time: 10:49
 */
@RestController
public class ActuaryOpeController implements ActuaryOpeAPI {
    @Autowired
    private ActuaryOpeService actuaryOpeService;

    @Override
    @ApiMethod
    public ServerResponse actuary(String houseId, String cityId,Integer type) {
        return actuaryOpeService.actuary(houseId, type);
    }
}
