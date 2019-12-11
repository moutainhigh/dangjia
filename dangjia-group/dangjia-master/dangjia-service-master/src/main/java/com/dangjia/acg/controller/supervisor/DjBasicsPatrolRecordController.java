package com.dangjia.acg.controller.supervisor;

import com.dangjia.acg.api.supervisor.DjBasicsPatrolRecordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.supervisor.PatrolRecordServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DjBasicsPatrolRecordController implements DjBasicsPatrolRecordAPI {
    @Autowired
    private PatrolRecordServices patrolRecordServices ;

    @Override
    @ApiMethod
    public ServerResponse addDjBasicsPatrolRecord(HttpServletRequest request,String userToken,String houseId,String images,String content) {
        return patrolRecordServices.addDjBasicsPatrolRecord(request,userToken,houseId,images,content);
    }



    @Override
    @ApiMethod
    public ServerResponse queryDjBasicsPatrolRecord(HttpServletRequest request, String userToken) {
        return patrolRecordServices.queryDjBasicsPatrolRecord(request,userToken);
    }
}
