package com.dangjia.acg.controller.web.engineer;

import com.dangjia.acg.api.web.engineer.DjMaintenanceRecordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.engineer.DjMaintenanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 9:57
 */
@RestController
public class DjMaintenanceRecordController implements DjMaintenanceRecordAPI {

    @Autowired
    private DjMaintenanceRecordService djMaintenanceRecordService;

    @Override
    @ApiMethod
    public ServerResponse queryDjMaintenanceRecordList(PageDTO pageDTO, String searchKey) {
        return djMaintenanceRecordService.queryDjMaintenanceRecordList(pageDTO,searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse queryDjMaintenanceRecordDetail(String id) {
        return djMaintenanceRecordService.queryDjMaintenanceRecordDetail(id);
    }

    @Override
    @ApiMethod
    public ServerResponse setDjMaintenanceRecord(String id,Integer state) {
        return djMaintenanceRecordService.setDjMaintenanceRecord(id,state);
    }
}
