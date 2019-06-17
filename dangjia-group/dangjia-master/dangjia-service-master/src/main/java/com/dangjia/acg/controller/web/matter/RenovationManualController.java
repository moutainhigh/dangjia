package com.dangjia.acg.controller.web.matter;

import com.dangjia.acg.api.web.matter.WebRenovationManualAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.service.matter.RenovationManualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class RenovationManualController implements WebRenovationManualAPI {

    @Autowired
    private RenovationManualService renovationManualService;

    @Override
    @ApiMethod
    public ServerResponse queryRenovationManual(HttpServletRequest request, PageDTO pageDTO, String workerTypeId, String name) {
        return renovationManualService.queryRenovationManual(pageDTO, workerTypeId, name);
    }

    @Override
    @ApiMethod
    public ServerResponse addRenovationManual(HttpServletRequest request, RenovationManual renovationManual) {
        return renovationManualService.addRenovationManual(renovationManual);
    }

    @Override
    @ApiMethod
    public ServerResponse updateRenovationManual(HttpServletRequest request, RenovationManual renovationManual) {
        return renovationManualService.updateRenovationManual(renovationManual);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteRenovationManual(HttpServletRequest request, String id) {
        return renovationManualService.deleteRenovationManual(id);
    }

    @Override
    @ApiMethod
    public ServerResponse getRenovationManualById(HttpServletRequest request, String id) {
        return renovationManualService.getRenovationManualById(id);
    }

}
