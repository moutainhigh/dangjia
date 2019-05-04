package com.dangjia.acg.controller.web.design;

import com.dangjia.acg.api.web.design.WebDesignAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.design.DesignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/10 0010
 * Time: 16:18
 */
@RestController
public class WebDesignController implements WebDesignAPI {

    @Autowired
    private DesignService designService;

    @Override
    @ApiMethod
    public ServerResponse sendPictures(HttpServletRequest request, String houseId) {
        return designService.sendPictures(houseId);
    }

    @Override
    @ApiMethod
    public ServerResponse uploadPictures(HttpServletRequest request, String houseId, String designImageTypeId, String imageurl) {
        return designService.uploadPictures(houseId, designImageTypeId, imageurl);
    }

    @Override
    @ApiMethod
    public ServerResponse getDesignList(HttpServletRequest request, PageDTO pageDTO, int designerType, String searchKey) {
        return designService.getDesignList(pageDTO, designerType, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse getImagesList(HttpServletRequest request, String houseId) {
        return designService.getImagesList(houseId);
    }
}
