package com.dangjia.acg.controller.app.design;

import com.dangjia.acg.api.app.design.HouseDesignImageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.house.HouseRemark;
import com.dangjia.acg.service.design.DesignDataService;
import com.dangjia.acg.service.design.DesignerOperationService;
import com.dangjia.acg.service.design.QuantityRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:27
 */
@RestController
public class HouseDesignImageController implements HouseDesignImageAPI {

    @Autowired
    private DesignDataService designDataService;
    @Autowired
    private DesignerOperationService designerOperationService;
    @Autowired
    private QuantityRoomService quantityRoomService;

    @Override
    @ApiMethod
    public ServerResponse sendPictures(HttpServletRequest request, String houseId) {
        return designerOperationService.sendPictures(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getDesignList(HttpServletRequest request, PageDTO pageDTO, Integer designerType,
                                        String searchKey, String workerKey, String userId) {
        return designDataService.getDesignList(request, pageDTO, designerType, searchKey, workerKey, userId);
    }

    @Override
    @ApiMethod
    public ServerResponse checkPass(String userToken, String houseId, Integer type) {
        return designerOperationService.checkPass(userToken, houseId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse invalidHouse(String houseId) {
        return designerOperationService.invalidHouse(houseId);
    }


    @Override
    @ApiMethod
    public ServerResponse setPlaneMap(HttpServletRequest request, String userToken, String houseId, String userId, String image) {
        return designerOperationService.setPlaneMap(userToken, houseId, userId, image);
    }

    @Override
    @ApiMethod
    public ServerResponse setConstructionPlans(HttpServletRequest request, String userToken, String houseId, String userId, String imageJson, String productIds) {
        return designerOperationService.setConstructionPlans(userToken, houseId, userId, imageJson, productIds);
    }

    @Override
    @ApiMethod
    public ServerResponse getRecommendProduct(HttpServletRequest request, PageDTO pageDTO, String houseId, Integer type) {
        return quantityRoomService.getRecommendProduct(pageDTO, houseId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse addRecommendProduct(HttpServletRequest request, String houseId, Integer type, String productIds) {
        return quantityRoomService.addRecommendProduct(houseId, type, productIds);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteRecommendProduct(HttpServletRequest request, String rpId) {
        return quantityRoomService.deleteRecommendProduct(rpId);
    }

    @Override
    @ApiMethod
    public ServerResponse setQuantityRoom(HttpServletRequest request, String userToken, String userId, String houseId,
                                          String villageId, String houseType,
                                          String building, String unit, String number, BigDecimal square,
                                          BigDecimal buildSquare, String images, Integer elevator, String floor) {
        return quantityRoomService.setQuantityRoom(userToken, userId, houseId, villageId, houseType, building, unit,
                number, square, buildSquare, images, elevator, floor);
    }

    @Override
    @ApiMethod
    public ServerResponse isConfirmAddress(HttpServletRequest request, String houseId) {
        return quantityRoomService.isConfirmAddress(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getConfirmAddress(HttpServletRequest request, String houseId) {
        return quantityRoomService.getConfirmAddress(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getQuantityRoom(HttpServletRequest request, String houseId) {
        return designDataService.getQuantityRoom(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getPlaneMap(HttpServletRequest request, String houseId) {
        return designDataService.getPlaneMap(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getConstructionPlans(HttpServletRequest request, String houseId) {
        return designDataService.getConstructionPlans(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getDesign(HttpServletRequest request, String userToken, String houseId, Integer type) {
        return designDataService.getDesign(userToken, houseId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse getOdlQuantityRoomList(HttpServletRequest request, PageDTO pageDTO, String houseId, Integer type) {
        return designDataService.getOdlQuantityRoomList(pageDTO, houseId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse getIdQuantityRoom(HttpServletRequest request, String quantityRoomId) {
        return designDataService.getIdQuantityRoom(quantityRoomId);
    }


    @Override
    @ApiMethod
    public ServerResponse getHouseStatistics(String cityId, String workerTypeId, PageDTO pageDTO, String startDate, String endDate) {
        return designDataService.getHouseStatistics(cityId, workerTypeId, pageDTO, startDate, endDate);
    }

    @Override
    @ApiMethod
    public ServerResponse addHouseRemark(HttpServletRequest request, HouseRemark houseRemark, String userId) {
        return designDataService.addHouseRemark(houseRemark, userId);
    }


    @Override
    @ApiMethod
    public ServerResponse queryHouseRemark(HttpServletRequest request, PageDTO pageDTO,
                                           String remarkType, String houseId) {
        return designDataService.queryHouseRemark(pageDTO, remarkType, houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getArrOdlQuantityRoomList(HttpServletRequest request, String houseId) {
        return designDataService.getArrOdlQuantityRoomList(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getArrCountList(HttpServletRequest request, String houseId) {
        return designDataService.getArrCountList(houseId);
    }


}
