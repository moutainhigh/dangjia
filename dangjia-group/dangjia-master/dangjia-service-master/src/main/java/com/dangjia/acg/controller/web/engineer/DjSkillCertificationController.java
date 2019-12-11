package com.dangjia.acg.controller.web.engineer;

import com.dangjia.acg.api.web.engineer.DjSkillCertificationAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
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
    public ServerResponse querySkillsCertificationWaitingList(PageDTO pageDTO, Integer workerTypeId, String searchKey, String workerId) {
        return djSkillCertificationService.querySkillsCertificationWaitingList(pageDTO,workerTypeId,searchKey,workerId);
    }

    @Override
    @ApiMethod
    public ServerResponse querySkillCertificationSelectedList(PageDTO pageDTO, String searchKey, String workerId) {
        return djSkillCertificationService.querySkillCertificationSelectedList(pageDTO,searchKey,workerId);
    }

    @Override
    @ApiMethod
    public ServerResponse insertSkillCertification(String jsonStr, String workerId) {
        return djSkillCertificationService.insertSkillCertification(jsonStr,workerId);
    }
}
