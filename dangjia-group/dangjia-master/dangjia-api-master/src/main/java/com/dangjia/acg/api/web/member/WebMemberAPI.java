package com.dangjia.acg.api.web.member;

import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/3 0003
 * Time: 16:30
 * web端用户接口
 */
@FeignClient("dangjia-service-master")
@Api(value = "用户接口", description = "用户接口")
public interface WebMemberAPI {

    @PostMapping("web/member/getMemberList")
    @ApiOperation(value = "获取业主列表", notes = "获取业主列表")
    ServerResponse getMemberList(@RequestParam("request") HttpServletRequest request,
                                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize);
}
