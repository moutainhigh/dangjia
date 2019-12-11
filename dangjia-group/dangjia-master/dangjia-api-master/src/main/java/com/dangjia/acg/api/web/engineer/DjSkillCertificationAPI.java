package com.dangjia.acg.api.web.engineer;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
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
                                                       @RequestParam("workerId") String workerId);

    @PostMapping(value = "web/engineer/querySkillCertificationSelectedList")
    @ApiOperation(value = "技能认证已选列表", notes = "技能认证已选列表")
    ServerResponse querySkillCertificationSelectedList(@RequestParam("pageDTO") PageDTO pageDTO,
                                                       @RequestParam("searchKey") String searchKey,
                                                       @RequestParam("workerId") String workerId);

    @PostMapping(value = "web/engineer/insertSkillCertification")
    @ApiOperation(value = "技能认证", notes = "技能认证")
    ServerResponse insertSkillCertification(@RequestParam("jsonStr") String jsonStr,
                                            @RequestParam("workerId") String workerId);

}
