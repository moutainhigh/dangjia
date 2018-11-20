package com.dangjia.acg.controller.app.other;

import com.dangjia.acg.api.app.other.CityAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.other.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/11/1 0001
 * Time: 16:29
 */
@RestController
public class CityController implements CityAPI {

    @Autowired
    private CityService cityService;

    /**
     * 所有城市
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getAllCity(){
        return cityService.getAllCity();
    }
}
