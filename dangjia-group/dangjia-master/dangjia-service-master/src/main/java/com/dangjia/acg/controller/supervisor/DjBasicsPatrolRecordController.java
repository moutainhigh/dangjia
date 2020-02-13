package com.dangjia.acg.controller.supervisor;

import com.dangjia.acg.api.supervisor.DjBasicsPatrolRecordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.supervisor.PatrolRecordServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DjBasicsPatrolRecordController implements DjBasicsPatrolRecordAPI {
    @Autowired
    private PatrolRecordServices patrolRecordServices;

    @Override
    @ApiMethod
    public ServerResponse addPatrolRecord(HttpServletRequest request, String userToken, String houseId, String content, String images) {
        return patrolRecordServices.addPatrolRecord(userToken, houseId, content, images);
    }

    @Override
    @ApiMethod
    public ServerResponse getPatrolRecordList(HttpServletRequest request, PageDTO pageDTO, Integer type, String searchKey) {
        return patrolRecordServices.getPatrolRecordList(pageDTO, type, searchKey);
    }

    @Override
    @ApiMethod
    public ServerResponse getAppPatrolRecordList(HttpServletRequest request, String userToken, PageDTO pageDTO, Integer type) {
        return patrolRecordServices.getAppPatrolRecordList(userToken, pageDTO, type);
    }

    @Override
    @ApiMethod
    public ServerResponse getPatrolRecordDetails(HttpServletRequest request, String userToken, String patrolRecordId) {
        return patrolRecordServices.getPatrolRecordDetails(userToken, patrolRecordId);
    }

}
