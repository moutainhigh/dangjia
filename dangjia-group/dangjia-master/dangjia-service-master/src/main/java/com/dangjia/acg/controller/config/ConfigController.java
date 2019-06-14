package com.dangjia.acg.controller.config;

import com.dangjia.acg.api.config.ConfigApi;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/13
 * Time: 15:52
 */
@RestController
public class ConfigController implements ConfigApi {
    @Autowired
    private ConfigService configService;

    @Override
    @ApiMethod
    public ServerResponse editDistance(double distance,double radius) {
        return configService.editDistance(distance,radius);
    }

    @Override
    @ApiMethod
    public ServerResponse selectDistance() {
        return configService.selectDistance();
    }
}
