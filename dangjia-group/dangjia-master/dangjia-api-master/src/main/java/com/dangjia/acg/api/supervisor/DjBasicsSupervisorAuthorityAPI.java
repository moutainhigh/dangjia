package com.dangjia.acg.api.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.supervisor.DjBasicsSupervisorAuthorityDTO;
import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Api(description = "督导权限配置表")
@FeignClient("dangjia-service-master")
public interface DjBasicsSupervisorAuthorityAPI {

    @PostMapping("web/supervisor/delAuthority")
    @ApiOperation(value = "删除已选", notes = "删除已选")
    ServerResponse delAuthority(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("id") String id);

    @PostMapping("web/supervisor/searchAuthority")
    @ApiOperation(value = "搜索已选", notes = "搜索已选")
    ServerResponse searchAuthority(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("visitState") String visitState,
                                   @RequestParam("keyWord") String keyWord,
                                   @RequestParam("pageDTO")  PageDTO pageDTO);

    @PostMapping("web/supervisor/addAuthority")
    @ApiOperation(value = "增加已选", notes = "增加已选")
    ServerResponse addAuthority(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("DjBasicsSupervisorAuthority") DjBasicsSupervisorAuthority djBasicsSupervisorAuthority);
}
