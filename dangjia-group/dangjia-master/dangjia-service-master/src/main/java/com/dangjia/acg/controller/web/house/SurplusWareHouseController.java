package com.dangjia.acg.controller.web.house;

import com.dangjia.acg.api.web.house.SurplusWareHouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.SurplusWareHouse;
import com.dangjia.acg.service.house.SurplusWareDivertService;
import com.dangjia.acg.service.house.SurplusWareHouseItemService;
import com.dangjia.acg.service.house.SurplusWareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: ysl
 * Date: 2019/1/31 0005
 * Time: 15:40
 */
@RestController
public class SurplusWareHouseController implements SurplusWareHouseAPI {

    @Autowired
    private SurplusWareHouseService surplusWareHouseService;

    @Autowired
    private SurplusWareDivertService surplusWareDivertService;
    @Autowired
    private SurplusWareHouseItemService surplusWareHouseItemService;


    @Override
    @ApiMethod
    public ServerResponse getAllSurplusWareHouse(HttpServletRequest request, PageDTO pageDTO, Integer state, String address, String productName, String beginDate, String endDate) {
        return surplusWareHouseService.getAllSurplusWareHouse(pageDTO, state, address, productName, beginDate, endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse setSurplusWareHouse(HttpServletRequest request, SurplusWareHouse surplusWareHouse) {
        return surplusWareHouseService.setSurplusWareHouse(surplusWareHouse);
    }

    @Override
    @ApiMethod
    public ServerResponse addSurplusWareHouseItem(HttpServletRequest request, String jsonStr) {
        return surplusWareHouseService.addSurplusWareHouseItem(jsonStr);
    }

    @Override
    @ApiMethod
    public ServerResponse getAllSurplusWareHouseItemBySId(HttpServletRequest request, PageDTO pageDTO, String surplusWareHouseId) {
        return surplusWareHouseService.getAllSurplusWareHouseItemBySId(pageDTO, surplusWareHouseId);
    }

    @Override
    @ApiMethod
    public ServerResponse addSurplusWareDivertList(HttpServletRequest request, String jsonStr) {
        return surplusWareDivertService.addSurplusWareDivertList(jsonStr);
    }

    @Override
    @ApiMethod
    public ServerResponse getAllSurplusWareDivertListBySId(HttpServletRequest request, PageDTO pageDTO, String surplusWareHouseId) {
        return surplusWareDivertService.getAllSurplusWareDivertListBySId(pageDTO, surplusWareHouseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getAllProductsLikeAddressOrPName(HttpServletRequest request, PageDTO pageDTO, String address, String productName) {
        return surplusWareHouseItemService.getAllProductsLikeAddressOrPName(pageDTO, address, productName);
    }

    @Override
    @ApiMethod
    public ServerResponse getAllSurplusWareHouseListByPId(HttpServletRequest request, PageDTO pageDTO, String productId) {
        return surplusWareHouseItemService.getAllSurplusWareHouseListByPId(pageDTO, productId);
    }


}
