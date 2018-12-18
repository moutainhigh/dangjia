package com.dangjia.acg.controller.app.design;

import com.dangjia.acg.api.app.design.HouseDesignImageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.design.HouseDesignImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

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
    public ServerResponse designImageList(String houseId){
        return houseDesignImageService.designImageList(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse checkPass(String userToken,String houseId,int type){
        return houseDesignImageService.checkPass(houseId,type);
    }

    @Override
    @ApiMethod
    public ServerResponse checkDesign(String userToken, String houseId){
        return houseDesignImageService.checkDesign(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse upgradeDesign(String userToken, String houseId, String designImageTypeId, int selected){
        return houseDesignImageService.upgradeDesign(userToken,houseId,designImageTypeId,selected);
    }
}
