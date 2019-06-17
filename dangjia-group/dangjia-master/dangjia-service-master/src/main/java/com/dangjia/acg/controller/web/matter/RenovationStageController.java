package com.dangjia.acg.controller.web.matter;

import com.dangjia.acg.api.web.matter.WebRenovationStageAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.matter.RenovationStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RenovationStageController implements WebRenovationStageAPI {

    @Autowired
    private RenovationStageService renovationStageService;

    @Override
    @ApiMethod
    public ServerResponse queryRenovationStage(HttpServletRequest request) {
        return renovationStageService.queryRenovationStage();
    }

    @Override
    @ApiMethod
    public ServerResponse addRenovationStage(HttpServletRequest request, String name, String image) {
        return renovationStageService.addRenovationStage(name, image);
    }

    @Override
    @ApiMethod
    public ServerResponse updateRenovationStage(HttpServletRequest request, String id, String name, String image) {
        return renovationStageService.updateRenovationStage(id, name, image);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteRenovationStage(HttpServletRequest request, String id) {
        return renovationStageService.deleteRenovationStage(id);
    }


}
