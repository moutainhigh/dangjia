package com.dangjia.acg.api.web.engineer;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.core.WorkerType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 11/12/2019
 * Time: 上午 10:11
 */
@FeignClient("dangjia-service-master")
@Api(value = "技能认证", description = "技能认证")
public interface DjSkillCertificationAPI {

    @PostMapping(value = "web/engineer/querySkillsCertificationWaitingList")
    @ApiOperation(value = "技能认证待选列表", notes = "技能认证待选列表")
    ServerResponse querySkillsCertificationWaitingList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                       @RequestParam("workerTypeId") Integer workerTypeId,
                                                       @RequestParam("searchKey") String searchKey,
                                                       @RequestParam("skillCertificationId") String skillCertificationId,
                                                       @RequestParam("cityId") String cityId);

    @PostMapping(value = "web/engineer/querySkillCertificationSelectedList")
    @ApiOperation(value = "技能认证已选列表", notes = "技能认证已选列表")
    ServerResponse querySkillCertificationSelectedList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                       @RequestParam("searchKey") String searchKey,
                                                       @RequestParam("skillCertificationId") String skillCertificationId,
                                                       @RequestParam("type") Integer type,
                                                       @RequestParam("cityId") String cityId);

    @PostMapping(value = "web/engineer/insertSkillCertification")
    @ApiOperation(value = "工匠技能认证", notes = "工匠技能认证")
    ServerResponse insertSkillCertification(@RequestParam("jsonStr") String jsonStr,
                                            @RequestParam("skillCertificationId") String skillCertificationId,
                                            @RequestParam("cityId") String cityId);

    @PostMapping(value = "web/engineer/queryWorkerTypeSkillPackConfigurationList")
    @ApiOperation(value = "工种技能包配置列表", notes = "工种技能包配置列表")
    ServerResponse queryWorkerTypeSkillPackConfigurationList();

    @PostMapping(value = "web/engineer/queryWorkerTypeSkillPackConfigurationDetail")
    @ApiOperation(value = "工种技能包配置详情", notes = "工种技能包配置详情")
    ServerResponse queryWorkerTypeSkillPackConfigurationDetail(@RequestParam("workerTypeId") String workerTypeId);

    @PostMapping(value = "web/engineer/insertWorkerTypeSkillPackConfiguration")
    @ApiOperation(value = "工种技能包配置", notes = "工种技能包配置")
    ServerResponse insertWorkerTypeSkillPackConfiguration(@RequestParam("jsonStr") String jsonStr,
                                                          @RequestParam("workerType") WorkerType workerType,
                                                          @RequestParam("cityId") String cityId);

    @PostMapping(value = "web/engineer/deleteSkillCertification")
    @ApiOperation(value = "技能删除", notes = "技能删除")
    ServerResponse deleteSkillCertification(@RequestParam("id") String id);

}
