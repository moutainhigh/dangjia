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

    @Override
    @ApiMethod
    public ServerResponse queryWorkerRewardPunishRecord(HttpServletRequest request, PageDTO pageDTO, String type, String keyWord) {
        return patrolRecordServices.queryWorkerRewardPunishRecord(request,pageDTO ,type,keyWord);
    }

    @Override
    @ApiMethod
    public ServerResponse queryPatrolRecordDetail(HttpServletRequest request,@RequestParam("rewordPunishCorrelationId") String rewordPunishCorrelationId) {
        return patrolRecordServices.queryPatrolRecordDetail(request,rewordPunishCorrelationId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryRewardPunishRecordDetail(HttpServletRequest request, String id) {
        return patrolRecordServices.queryRewardPunishRecordDetail(request,id);
    }

    @Override
    @ApiMethod
    public ServerResponse getSupHomePage(HttpServletRequest request,PageDTO pageDTO,String userToken) {
        return patrolRecordServices.getSupHomePage(request,pageDTO,userToken);
    }
}
