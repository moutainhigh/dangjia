package com.dangjia.acg.controller.app.design;

import com.dangjia.acg.api.app.design.HouseDesignImageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.design.HouseDesignImageService;
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
    private HouseDesignImageService houseDesignImageService;


    /**
     * 查看施工图
     */
    @Override
    @ApiMethod
    public ServerResponse designImageList(String houseId) {
        return houseDesignImageService.designImageList(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse checkPass(String userToken, String houseId, int type) {
        return houseDesignImageService.checkPass(userToken, houseId, type);
    }

    @Override
    @ApiMethod
    public ServerResponse checkDesign(String userToken, String houseId) {
        return houseDesignImageService.checkDesign(userToken, houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse upgradeDesign(String userToken, String houseId, String designImageTypeId, int selected) {
        return houseDesignImageService.upgradeDesign(userToken, houseId, designImageTypeId, selected);
    }

    @Override
    @ApiMethod
    public ServerResponse setQuantityRoom(HttpServletRequest request, String userToken, String houseId, String userId, String images) {
        return houseDesignImageService.setQuantityRoom(userToken, houseId, userId, images);
    }

    @Override
    @ApiMethod
    public ServerResponse getQuantityRoom(HttpServletRequest request, String houseId) {
        return houseDesignImageService.getQuantityRoom(houseId);
    }

}
