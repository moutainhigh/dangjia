package com.dangjia.acg.controller.web.city;

import com.dangjia.acg.api.web.city.WebCityAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.service.city.WebCityServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/1
 * Time: 15:33
 */
@RestController
public class WebCityController implements WebCityAPI {
    @Autowired
    private WebCityServices cityServices;

    @Override
    @ApiMethod
    public ServerResponse getCityList(String cityId) {
        return cityServices.getCityList(cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse addCity(City city) {
        return cityServices.addCity(city);
    }

    @Override
    @ApiMethod
    public ServerResponse delCity(String cityId) {
        return cityServices.delCity(cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse updateCity(City city) {
        return cityServices.updateCity(city);
    }
}
