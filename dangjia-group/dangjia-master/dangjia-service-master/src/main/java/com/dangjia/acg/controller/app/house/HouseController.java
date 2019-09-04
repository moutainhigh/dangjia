package com.dangjia.acg.controller.app.house;

import com.dangjia.acg.api.app.house.HouseAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.other.IndexPageService;
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
    @Autowired
    private IndexPageService indexPageService;


    /**
     * 切换房产
     */
    @Override
    @ApiMethod
    public ServerResponse setSelectHouse(String userToken, String houseId) {
        return houseService.setSelectHouse(userToken, houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getMyHouseList(PageDTO pageDTO, String userToken) {
        return houseService.getMyHouseList(pageDTO, userToken);
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
    public ServerResponse setStartHouse(String userToken, String cityId, Integer houseType, Integer drawings,
                                        String latitude, String longitude, String address, String name) {
        return houseService.setStartHouse(userToken, cityId, houseType, drawings, latitude, longitude, address, name);
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
        return indexPageService.queryHouseByCity(userToken, cityId, villageId, minSquare, maxSquare, houseType, pageDTO);
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
     * @param houseId 房子ID @Instance: 835240771552816792532
     * @param pageDTO 分页内容
     * @return 施工记录列表 @see swagger:房产接口-> 施工记录
     */
    @Override
    @ApiMethod
    public ServerResponse queryConstructionRecord(String houseId, String day, String workerType, PageDTO pageDTO) {
//        return  houseService.queryConstructionRecord(houseId, pageDTO, null);
        return houseService.queryConstructionRecordAll(houseId, null, day, workerType, 0, pageDTO);
    }

    /**
     * 施工记录
     *
     * @param houseId 房子ID @Instance: 835240771552816792532
     * @param pageDTO 分页内容
     * @return 施工记录列表 @see swagger:房产接口-> 施工记录
     */
    @Override
    @ApiMethod
    public ServerResponse queryConstructionRecordAll(String houseId, String ids, String day, String workerType, Integer type, PageDTO pageDTO) {
        return houseService.queryConstructionRecordAll(houseId, ids, day, workerType, type, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse queryConstructionRecordType(String houseId) {
        return houseService.queryConstructionRecordType(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getStageProgress(String houseFlowId) {
        return houseService.getStageProgress(houseFlowId);
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
            Double minSquare = square - 15;
            Double maxSquare = square + 15;
            return houseService.getReferenceBudget(cityId, villageId, minSquare, maxSquare, houseType);
        } else {
            return ServerResponse.createByErrorMessage("请输入正确的面积");
        }
    }

    @Override
    @ApiMethod
    public ServerResponse updateByHouseId(String building, String unit, String number, String houseId,
                                          String villageId, String cityId, Double buildSquare) {
        return houseService.updateByHouseId(building, unit, number, houseId, villageId, cityId, buildSquare);
    }


    @Override
    @ApiMethod
    public ServerResponse updateCustomEdit(String houseId) {
        return houseService.updateCustomEdit(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getHouseChoiceCases(String id) {
        return houseService.getHouseChoiceCases(id);
    }

}
