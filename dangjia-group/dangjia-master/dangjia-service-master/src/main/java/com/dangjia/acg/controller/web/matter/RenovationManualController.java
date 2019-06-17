package com.dangjia.acg.controller.web.matter;

import com.dangjia.acg.api.web.matter.WebRenovationManualAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.service.matter.RenovationManualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RenovationManualController implements WebRenovationManualAPI {

    @Autowired
    private RenovationManualService renovationManualService;

    @Override
    @ApiMethod
    public ServerResponse queryRenovationManual(PageDTO pageDTO, RenovationManual renovationManual) {
        return renovationManualService.queryRenovationManual(pageDTO, renovationManual);
    }

    @Override
    @ApiMethod
    public ServerResponse addRenovationManual(RenovationManual renovationManual) {
        return renovationManualService.addRenovationManual(renovationManual);
    }

    @Override
    @ApiMethod
    public ServerResponse updateRenovationManual(RenovationManual renovationManual) {
        return renovationManualService.updateRenovationManual(renovationManual);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteRenovationManual(String id) {
        return renovationManualService.deleteRenovationManual(id);
    }

    @Override
    @ApiMethod
    public ServerResponse getRenovationManualById(String id) {
        return renovationManualService.getRenovationManualById(id);
    }

}
