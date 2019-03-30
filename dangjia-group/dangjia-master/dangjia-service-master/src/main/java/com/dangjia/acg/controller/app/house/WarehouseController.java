package com.dangjia.acg.controller.app.house;

import com.dangjia.acg.api.app.house.WarehouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.house.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class WarehouseController implements WarehouseAPI {

    @Autowired
    private WarehouseService warehouseService;

    /**
     * 购买的材料
     */
    @Override
    @ApiMethod
    public ServerResponse warehouseList(HttpServletRequest request, String houseId, String name, Integer type){
        return warehouseService.warehouseList(request,houseId,name,type);
    }
}
