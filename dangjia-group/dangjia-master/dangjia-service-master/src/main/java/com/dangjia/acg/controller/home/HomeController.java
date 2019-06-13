package com.dangjia.acg.controller.home;

import com.dangjia.acg.api.home.HomeAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.home.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HomeController implements HomeAPI {

    @Autowired
    private HomeService homeService;

    @Override
    @ApiMethod
    public ServerResponse getAppHomeCollocation(HttpServletRequest request) {
        return homeService.getAppHomeCollocation();
    }

    @Override
    @ApiMethod
    public ServerResponse setAppHomeCollocation(HttpServletRequest request, String userId, String masterpieceIds) {
        return homeService.setAppHomeCollocation(userId, masterpieceIds);
    }

    @Override
    @ApiMethod
    public ServerResponse getAppHomeCollocationHistory(HttpServletRequest request, PageDTO pageDTO) {
        return homeService.getAppHomeCollocationHistory(pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse getHomeMasterplateList(HttpServletRequest request, PageDTO pageDTO) {
        return homeService.getHomeMasterplateList(pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse addHomeMasterplate(HttpServletRequest request, String name, String image, String url, String userId) {
        return homeService.addHomeMasterplate(name, image, url, userId);
    }

    @Override
    @ApiMethod
    public ServerResponse delHomeMasterplate(HttpServletRequest request, String id) {
        return homeService.delHomeMasterplate(id);
    }

    @Override
    @ApiMethod
    public ServerResponse upDataHomeMasterplate(HttpServletRequest request, String id, String name, String image, String url, String userId) {
        return homeService.upDataHomeMasterplate(id, name, image, url, userId);
    }
}
