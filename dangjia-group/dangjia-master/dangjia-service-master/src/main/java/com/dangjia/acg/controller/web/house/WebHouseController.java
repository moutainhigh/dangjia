package com.dangjia.acg.controller.web.house;

import com.dangjia.acg.api.web.house.WebHouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.service.house.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 15:40
 */
@RestController
public class WebHouseController implements WebHouseAPI {

    @Autowired
    private HouseService houseService;

    @Override
    @ApiMethod
    public ServerResponse getList(HttpServletRequest request, PageDTO pageDTO, Integer visitState, String startDate, String endDate, String searchKey, String orderBy, String memberId) {
        return houseService.getList(pageDTO, visitState, startDate, endDate, searchKey, orderBy, memberId);
    }

    @Override
    @ApiMethod
    public ServerResponse startWorkPage(HttpServletRequest request, String houseId) {
        return houseService.startWorkPage(request, houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse startWork(HttpServletRequest request, HouseDTO houseDTO, String members, String prefixs) {
        return houseService.startWork(request, houseDTO, members, prefixs);
    }

    @Override
    @ApiMethod
    public ServerResponse setHouseInfo(HttpServletRequest request, House house) {
        return houseService.setHouseInfo(house);
    }

    @Override
    @ApiMethod
    public ServerResponse queryConstructionRecord(String houseId, PageDTO pageDTO, String workerTypeId) {
        return houseService.queryConstructionRecord(houseId, pageDTO, workerTypeId);
    }

    @Override
    @ApiMethod
    public ServerResponse getAllHouseByVisitState(Integer visitState) {
        return houseService.getAllHouseByVisitState(visitState);
    }

    @Override
    @ApiMethod
    public ServerResponse getHistoryWorker(String houseId, String workerTypeId, String workId, PageDTO pageDTO) {
        return houseService.getHistoryWorker(houseId, workerTypeId, workId, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseProfitList(PageDTO pageDTO, String visitState,  String searchKey){
        return houseService.getHouseProfitList(pageDTO, visitState, searchKey);
    }
}
