package com.dangjia.acg.controller.sale.store;

import com.dangjia.acg.api.sale.store.StoreManagementAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.sale.residential.ResidentialBuilding;
import com.dangjia.acg.service.sale.store.StoreManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/25
 * Time: 13:56
 */
@RestController
public class StoreManagementController implements StoreManagementAPI {

    @Autowired
    private StoreManagementService storeManagementService;

    @Override
    @ApiMethod
    public ServerResponse storeManagementPage(HttpServletRequest request, String userToken, PageDTO pageDTO) {
        return storeManagementService.storeManagementPage(userToken,pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse addBuilding(HttpServletRequest request, String villageId, Date modifyDate, String building,String storeId) {
        return storeManagementService.addBuilding(villageId,modifyDate,building,storeId);
    }

    @Override
    @ApiMethod
    public ServerResponse delBuilding(HttpServletRequest request,String buildingId) {
        return storeManagementService.delBuilding(buildingId);
    }

    @Override
    @ApiMethod
    public ServerResponse updatBuilding(HttpServletRequest request, String buildingId , ResidentialBuilding residentialBuilding) {
        return storeManagementService.updatBuilding(buildingId,residentialBuilding);
    }

    @Override
    @ApiMethod
    public ServerResponse BuildingList(HttpServletRequest request,String storeId,PageDTO pageDTO) {
        return storeManagementService.BuildingList(storeId,pageDTO);
    }


}
