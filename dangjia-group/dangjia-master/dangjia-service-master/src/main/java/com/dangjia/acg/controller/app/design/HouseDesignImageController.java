package com.dangjia.acg.controller.app.design;

import com.dangjia.acg.api.app.design.HouseDesignImageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.design.DesignDataService;
import com.dangjia.acg.service.design.DesignerOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    @Override
    @ApiMethod
    public ServerResponse sendPictures(HttpServletRequest request, String houseId) {
        return designerOperationService.sendPictures(houseId);
    }

//    @Overrideerride
//    @ApiMethod
//    public ServerResponse uploadPictures(HttpServletRequest request, String houseId, String designImageTypeId, String imageurl) {
//        return designDataService.uploadPictures(houseId, designImageTypeId, imageurl);
//    }

    @Override
    @ApiMethod
    public ServerResponse getDesignList(HttpServletRequest request, PageDTO pageDTO, int designerType, String searchKey) {
        return designDataService.getDesignList(pageDTO, designerType, searchKey);
    }

//    @Override
//    @ApiMethod
//    public ServerResponse getImagesList(HttpServletRequest request, String houseId) {
//        return designDataService.getImagesList(houseId);
//    }

//    /**
//     * 查看施工图
//     */
//    @Override
//    @ApiMethod
//    public ServerResponse designImageList(String houseId) {
//        return designDataService.designImageList(houseId);
//    }

    @Override
    @ApiMethod
    public ServerResponse checkPass(String userToken, String houseId, int type) {
        return designerOperationService.checkPass(userToken, houseId, type);
    }

//    @Override
//    @ApiMethod
//    public ServerResponse checkDesign(String userToken, String houseId) {
//        return designDataService.checkDesign(userToken, houseId);
//    }

    @Override
    @ApiMethod
    public ServerResponse invalidHouse(String houseId) {
        return designerOperationService.invalidHouse(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse upgradeDesign(String userToken, String houseId, String designImageTypeId, int selected) {
        return designerOperationService.upgradeDesign(userToken, houseId, designImageTypeId, selected);
    }

    @Override
    @ApiMethod
    public ServerResponse setPlaneMap(HttpServletRequest request, String userToken, String houseId, String userId, String image) {
        return designerOperationService.setPlaneMap(userToken, houseId, userId, image);
    }

    @Override
    @ApiMethod
    public ServerResponse setConstructionPlans(HttpServletRequest request, String userToken, String houseId, String userId, String imageJson) {
        return designerOperationService.setConstructionPlans(userToken, houseId, userId, imageJson);
    }

    @Override
    @ApiMethod
    public ServerResponse setQuantityRoom(HttpServletRequest request, String userToken, String houseId, String userId, String images) {
        return designerOperationService.setQuantityRoom(userToken, houseId, userId, images);
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
    public ServerResponse getDesign(HttpServletRequest request, String userToken, String houseId) {
        return designDataService.getDesign(userToken, houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse getOdlQuantityRoomList(HttpServletRequest request, PageDTO pageDTO, String houseId, int type) {
        return designDataService.getOdlQuantityRoomList(pageDTO, houseId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse getIdQuantityRoom(HttpServletRequest request, String quantityRoomId) {
        return designDataService.getIdQuantityRoom(quantityRoomId);
    }

}
