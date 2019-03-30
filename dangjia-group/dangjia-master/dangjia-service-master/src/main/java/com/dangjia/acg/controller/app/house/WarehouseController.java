package com.dangjia.acg.controller.app.house;

import com.dangjia.acg.api.app.house.WarehouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.house.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ServerResponse warehouseList(PageDTO pageDTO, String houseId, String categoryId, String name, Integer type){
        return warehouseService.warehouseList(pageDTO.getPageNum(),pageDTO.getPageSize(),houseId,categoryId,name,type);
    }

    /**
     * 购买的材料
     */
    @Override
    @ApiMethod
    public ServerResponse warehouseGmList(HttpServletRequest request, String houseId, String name, Integer type){
        return warehouseService.warehouseGmList(request,houseId,name,type);
    }
}
