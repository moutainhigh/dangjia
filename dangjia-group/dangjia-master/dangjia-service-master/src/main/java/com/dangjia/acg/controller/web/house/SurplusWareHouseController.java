package com.dangjia.acg.controller.web.house;

import com.dangjia.acg.api.web.house.SurplusWareHouseAPI;
import com.dangjia.acg.api.web.house.WebHouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.SurplusWareDivert;
import com.dangjia.acg.modle.house.SurplusWareHouse;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.house.SurplusWareDivertService;
import com.dangjia.acg.service.house.SurplusWareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 15:40
 */
@RestController
public class SurplusWareHouseController implements SurplusWareHouseAPI {

    @Autowired
    private SurplusWareHouseService surplusWareHouseService;

    @Autowired
    private SurplusWareDivertService surplusWareDivertService;

    @Override
    @ApiMethod
    public ServerResponse getAllSurplusWareHouse(HttpServletRequest request, PageDTO pageDTO, Integer state, String beginDate, String endDate) {
        return surplusWareHouseService.getAllSurplusWareHouse(pageDTO, state, beginDate, endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse setSurplusWareHouse(HttpServletRequest request, SurplusWareHouse surplusWareHouse) {
        return surplusWareHouseService.setSurplusWareHouse(surplusWareHouse);
    }

    @Override
    public ServerResponse addSurplusWareHouseItem(HttpServletRequest request, String jsonStr) {
        return surplusWareHouseService.addSurplusWareHouseItem(jsonStr);
    }

    @Override
    public ServerResponse getAllSurplusWareHouseItemBySId(HttpServletRequest request, PageDTO pageDTO, String surplusWareHouseId) {
        return surplusWareHouseService.getAllSurplusWareHouseItemBySId(pageDTO, surplusWareHouseId);
    }

    @Override
    public ServerResponse addSurplusWareDivertList(HttpServletRequest request, String jsonStr) {
        return surplusWareDivertService.addSurplusWareDivertList(jsonStr);
    }

    @Override
    public ServerResponse getAllSurplusWareDivertListBySId(HttpServletRequest request, PageDTO pageDTO, String surplusWareHouseId) {
        return surplusWareDivertService.getAllSurplusWareDivertListBySId(pageDTO, surplusWareHouseId);
    }


}
