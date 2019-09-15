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


    @RequestMapping(value = "member/collect/queryHouse", method = RequestMethod.POST)
    @ApiOperation(value = "查询收藏的工地记录", notes = "查询收藏的工地记录")
    ServerResponse queryCollectHouse(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("userToken") String userToken,
                                     @RequestParam("pageDTO") PageDTO pageDTO);

    @RequestMapping(value = "member/collect/queryGood", method = RequestMethod.POST)
    @ApiOperation(value = "查询收藏的商品记录", notes = "查询收藏的商品记录")
    ServerResponse queryCollectGood(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("userToken") String userToken,
                                     @RequestParam("pageDTO") PageDTO pageDTO);


    @RequestMapping(value = "member/collect/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加收藏", notes = "添加收藏，conditionType:0->代表收藏房子 1->代表收藏商品")
    ServerResponse addMemberCollect(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("houseId") String houseId,@RequestParam("conditionType") String conditionType);

    @RequestMapping(value = "member/collect/check", method = RequestMethod.POST)
    @ApiOperation(value = "检测是否收藏", notes = "检测是否收藏，conditionType:0->代表收藏房子 1->代表收藏商品")
    ServerResponse isMemberCollect(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("houseId") String houseId,@RequestParam("conditionType") String conditionType);

    @RequestMapping(value = "member/collect/del", method = RequestMethod.POST)
    @ApiOperation(value = "取消收藏", notes = "取消收藏,conditionType:0->代表收藏房子 1->代表收藏商品")
    ServerResponse delMemberCollect(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("houseId") String houseId,@RequestParam("conditionType") String conditionType);


    /**
     * 我的收藏：查看更多工地 需求有问题，待开发
     */

    /**
     * 推荐用户点击最多类别的商品，在类别内随机推荐   需求有问题，待开发
     */

}

