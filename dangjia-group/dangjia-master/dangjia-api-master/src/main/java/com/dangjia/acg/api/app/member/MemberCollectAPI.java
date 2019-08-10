package com.dangjia.acg.api.app.member;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@FeignClient("dangjia-service-master")
@Api(value = "当家用户收藏接口", description = "当家用户收藏接口")
public interface MemberCollectAPI {

    @RequestMapping(value = "member/collect/query", method = RequestMethod.POST)
    @ApiOperation(value = "查询收藏的工地记录", notes = "查询收藏的工地记录")
    ServerResponse queryCollectHouse(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("userToken") String userToken,
                                     @RequestParam("pageDTO") PageDTO pageDTO);

    @RequestMapping(value = "member/collect/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加收藏", notes = "添加收藏")
    ServerResponse addMemberCollect(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("houseId") String houseId);

    @RequestMapping(value = "member/collect/check", method = RequestMethod.POST)
    @ApiOperation(value = "检测是否收藏", notes = "检测是否收藏")
    ServerResponse isMemberCollect(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("houseId") String houseId);


    @RequestMapping(value = "member/collect/del", method = RequestMethod.POST)
    @ApiOperation(value = "取消收藏", notes = "取消收藏")
    ServerResponse delMemberCollect(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("houseId") String houseId);
}

