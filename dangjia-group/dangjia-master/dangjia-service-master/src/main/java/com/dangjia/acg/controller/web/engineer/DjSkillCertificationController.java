package com.dangjia.acg.controller.web.engineer;

import com.dangjia.acg.api.web.engineer.DjSkillCertificationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.service.engineer.DjSkillCertificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 11/12/2019
 * Time: 上午 10:14
 */
@RestController
public class DjSkillCertificationController implements DjSkillCertificationAPI {

    @Autowired
    private DjSkillCertificationService djSkillCertificationService;


    @Override
    @ApiMethod
    public ServerResponse querySkillsCertificationWaitingList(PageDTO pageDTO,Integer workerTypeId, String searchKey, String skillCertificationId, String cityId) {
        return djSkillCertificationService.querySkillsCertificationWaitingList(pageDTO,workerTypeId,searchKey,skillCertificationId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryWorkerTypeSkillWaitingList(PageDTO pageDTO, Integer workerTypeId, String searchKey, String skillCertificationId, String cityId) {
        return djSkillCertificationService.queryWorkerTypeSkillWaitingList(pageDTO,workerTypeId,searchKey,skillCertificationId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse querySkillCertificationSelectedList(PageDTO pageDTO, String searchKey, String skillCertificationId, Integer type, String cityId) {
        return djSkillCertificationService.querySkillCertificationSelectedList(pageDTO,searchKey,skillCertificationId,type,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse insertSkillCertification(String jsonStr, String skillCertificationId, String cityId) {
        return djSkillCertificationService.insertSkillCertification(jsonStr,skillCertificationId,cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse queryWorkerTypeSkillPackConfigurationList() {
        return djSkillCertificationService.queryWorkerTypeSkillPackConfigurationList();
    }

    @Override
    @ApiMethod
    public ServerResponse queryWorkerTypeSkillPackConfigurationDetail(String workerTypeId) {
        return djSkillCertificationService.queryWorkerTypeSkillPackConfigurationDetail(workerTypeId);
    }

    @Override
    @ApiMethod
    public ServerResponse insertWorkerTypeSkillPackConfiguration(String jsonStr, WorkerType workerType, String cityId) {
        return djSkillCertificationService.insertWorkerTypeSkillPackConfiguration(jsonStr, workerType, cityId);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteSkillCertification(String id) {
        return djSkillCertificationService.deleteSkillCertification(id);
    }
}
