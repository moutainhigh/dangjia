package com.dangjia.acg.controller.app.house;

import com.dangjia.acg.api.app.house.WarehouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.house.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WarehouseController implements WarehouseAPI {

    @Autowired
    private WarehouseService warehouseService;

    /**
     * 购买的材料
     */
    @Override
    @ApiMethod
    public ServerResponse warehouseList(Integer pageNum, Integer pageSize, String houseId, String categoryId, String name,Integer type){
        return warehouseService.warehouseList(pageNum,pageSize,houseId,categoryId,name,type);
    }
}
