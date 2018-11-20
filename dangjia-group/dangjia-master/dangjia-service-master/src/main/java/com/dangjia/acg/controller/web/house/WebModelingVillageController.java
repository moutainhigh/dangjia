package com.dangjia.acg.controller.web.house;

import com.dangjia.acg.api.web.house.WebModelingVillageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.house.ModelingVillageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/9 0009
 * Time: 17:41
 */
@RestController
public class WebModelingVillageController implements WebModelingVillageAPI {

    @Autowired
    private ModelingVillageService modelingVillageService;

    @Override
    @ApiMethod
    public ServerResponse getCityList(){
        return modelingVillageService.getCityList();
    }

    @Override
    @ApiMethod
    public ServerResponse getVillageList(HttpServletRequest request,String cityId){
        return modelingVillageService.getVillageList(request,cityId);
    }
    @Override
    @ApiMethod
    public ServerResponse getLayoutList(HttpServletRequest request,String villageId){
        return modelingVillageService.getLayoutList(request,villageId);
    }
    @Override
    @ApiMethod
    public ServerResponse getHouseList(HttpServletRequest request,String modelingLayoutId){
        return modelingVillageService.getHouseList(request,modelingLayoutId);
    }
}
