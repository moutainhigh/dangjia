package com.dangjia.acg.controller.app.repair;

import com.dangjia.acg.api.app.repair.MasterMendWorkerAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.repair.MendWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/15
 * Time: 19:28
 */
@RestController
public class MendWorkerController implements MasterMendWorkerAPI {
    @Autowired
    private MendWorkerService mendWorkerService;

    @Override
    @ApiMethod
    public ServerResponse updateMendWorker(String lists) {
        return mendWorkerService.updateMendWorkerById(lists);
    }
}
