package com.dangjia.acg.controller.app.house;

import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.service.house.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/11/2 0002
 * Time: 19:52
 */
@RestController
public class HouseController implements HouseAPI {

    @Autowired
    private HouseService houseService;

    /**
     * 切换房产
     */
    @Override
    @ApiMethod
    public ServerResponse setSelectHouse(String userToken, String cityId, String houseId) {
        return houseService.setSelectHouse(userToken, cityId, houseId);
    }

    /**
     * 房产列表
     */
    @Override
    @ApiMethod
    public ServerResponse getHouseList(String userToken, String cityId) {
        return houseService.getHouseList(userToken, cityId);
    }

    /**
     * 我的房产
     */
    @Override
    @ApiMethod
    public ServerResponse getMyHouse(String userToken, String cityId) {
        return houseService.getMyHouse(userToken, cityId);
    }

    /**
     * 我的房产
     */
    @Override
    @ApiMethod
    public ServerResponse queryMyHouse(String userToken) {
        return houseService.queryMyHouse(userToken);
    }

    /**
     * @param userToken
     * @param houseType 装修的房子类型0：新房；1：老房
     * @param drawings  有无图纸0：无图纸；1：有图纸
     */
    @Override
    @ApiMethod
    public ServerResponse setStartHouse(String userToken, String cityId, int houseType, int drawings) {
        return houseService.setStartHouse(userToken, cityId, houseType, drawings);
    }

    /**
     * 撤销房子装修
     *
     * @param userToken
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse revokeHouse(@RequestParam("userToken") String userToken) {
        return houseService.revokeHouse(userToken);
    }

    /**
     * 修改房子精算状态
     *
     * @param houseId
     * @param budgetOk
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setHouseBudgetOk(String houseId, Integer budgetOk) {
        return houseService.setHouseBudgetOk(houseId, budgetOk);
    }

    /**
     * app修改房子精算状态
     *
     * @param houseId
     * @param budgetOk
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse setHouseBudgetOk(String userToken, String houseId, Integer budgetOk) {
        return houseService.setHouseBudgetOk(houseId, budgetOk);
    }

    //根据城市，小区，最小最大面积查询房子
    @Override
    @ApiMethod
    public ServerResponse queryHouseByCity(String userToken, String cityId, String villageId, Double minSquare, Double maxSquare, Integer houseType, PageDTO pageDTO) {
        return houseService.queryHouseByCity(userToken, cityId, villageId, minSquare, maxSquare, houseType, pageDTO);
    }

    //装修指南
    @Override
    @ApiMethod
    public ServerResponse getRenovationManual(String userToken, Integer type) {
        return houseService.getRenovationManual(userToken, type);
    }

    @Override
    @ApiMethod
    public ServerResponse getRenovationManualinfo(String id) {
        return houseService.getRenovationManualinfo(id);
    }

    /**
     * 保存装修指南
     *
     * @param userToken
     * @param saveList
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse saveRenovationManual(String userToken, String saveList) {
        return houseService.saveRenovationManual(userToken, saveList);
    }

    /**
     * 施工记录
     *
     * @param houseId
     * @param pageDTO
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryConstructionRecord(String houseId, PageDTO pageDTO) {
        return houseService.queryConstructionRecord(houseId, pageDTO.getPageNum(), pageDTO.getPageSize(), null);
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseFlowApply(String houseFlowApplyId) {
        return houseService.getHouseFlowApply(houseFlowApplyId);
    }

    /**
     * 工序记录
     */
    @Override
    @ApiMethod
    public ServerResponse queryFlowRecord(String houseFlowId) {
        return houseService.queryFlowRecord(houseFlowId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryHomeConstruction() {
        return houseService.queryHomeConstruction();
    }

    /**
     * 根据id查询房子信息
     *
     * @param houseId
     * @return
     */
    @Override
    @ApiMethod
    public House getHouseById(String houseId) {
        return houseService.getHouseById(houseId);
    }

    /**
     * 参考报价
     *
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse getReferenceBudget(String cityId, String villageId, Double square, Integer houseType) {
        if (square != null && !"".equals(square)) {
            Double minSquare = square - 50;
            Double maxSquare = square + 50;
            return houseService.getReferenceBudget(cityId, villageId, minSquare, maxSquare, houseType);
        } else {
            return ServerResponse.createByErrorMessage("请输入正确的面积");
        }
    }
    @Override
    @ApiMethod
    public ServerResponse updateByHouseId(String building,String unit,String number,String houseId,String villageId,String cityId,String modelingLayoutId) {
       return  houseService.updateByHouseId(building,unit,number,houseId,villageId,cityId,modelingLayoutId);
    }

}
