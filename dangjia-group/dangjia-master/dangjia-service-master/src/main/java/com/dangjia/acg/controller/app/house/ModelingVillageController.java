package com.dangjia.acg.controller.app.house;

import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.api.app.house.ModelingVillageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.house.ModelingVillageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小区管理类
 */
@RestController
public class ModelingVillageController implements ModelingVillageAPI {

    @Autowired
    private ModelingVillageService modelingVillageService;

    /**
     * 根据城市查询小区
     */
    @Override
    @ApiMethod
    public ServerResponse getAllVillageByCity(String cityId){
        return modelingVillageService.getAllVillageByCity(cityId);
    }


}
