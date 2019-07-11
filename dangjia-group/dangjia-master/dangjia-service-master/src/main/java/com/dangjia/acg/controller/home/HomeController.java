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
    public ServerResponse addHomeTemplate(HttpServletRequest request, String userId, String name) {
        return homeService.addHomeTemplate(userId, name);
    }

    @Override
    @ApiMethod
    public ServerResponse getHomeTemplateList(HttpServletRequest request, PageDTO pageDTO) {
        return homeService.getHomeTemplateList(pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse upDataHomeTemplate(HttpServletRequest request, String userId, String name, String templateId) {
        return homeService.upDataHomeTemplate(userId, name, templateId);
    }

    @Override
    @ApiMethod
    public ServerResponse setHomeTemplateEnable(HttpServletRequest request, String userId, String templateId) {
        return homeService.setHomeTemplateEnable(userId, templateId);
    }

    @Override
    @ApiMethod
    public ServerResponse delHomeTemplate(HttpServletRequest request, String userId, String templateId) {
        return homeService.delHomeTemplate(userId, templateId);
    }

    @Override
    @ApiMethod
    public ServerResponse getAppHomeCollocation(HttpServletRequest request, String templateId) {
        return homeService.getAppHomeCollocation(templateId);
    }

    @Override
    @ApiMethod
    public ServerResponse setAppHomeCollocation(HttpServletRequest request, String templateId, String userId, String masterpieceIds) {
        return homeService.setAppHomeCollocation(templateId, userId, masterpieceIds);
    }

    @Override
    @ApiMethod
    public ServerResponse getAppHomeCollocationHistory(HttpServletRequest request, String templateId, PageDTO pageDTO) {
        return homeService.getAppHomeCollocationHistory(templateId, pageDTO);
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
    public ServerResponse delHomeMasterplate(HttpServletRequest request, String id, String userId) {
        return homeService.delHomeMasterplate(id, userId);
    }

    @Override
    @ApiMethod
    public ServerResponse upDataHomeMasterplate(HttpServletRequest request, String id, String name, String image, String url, String userId) {
        return homeService.upDataHomeMasterplate(id, name, image, url, userId);
    }
}
