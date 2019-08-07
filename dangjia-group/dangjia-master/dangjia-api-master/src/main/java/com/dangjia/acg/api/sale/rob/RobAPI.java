package com.dangjia.acg.api.sale.rob;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.sale.rob.CustomerRecDTO;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.home.IntentionHouse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * 抢单模块 API
 * author: ljl
 * Date: 2019/7/27
 * Time: 9:59
 */
@FeignClient("dangjia-service-master")
@Api(value = "抢单模块", description = "抢单模块")
public interface RobAPI {


    @PostMapping(value = "sale/rob/queryRobSingledata")
    @ApiOperation(value = "抢单列表查询", notes = "抢单列表查询")
    ServerResponse queryRobSingledata(@RequestParam("request")HttpServletRequest request,
                                      @RequestParam("userToken")String userToken,
                                      @RequestParam("storeId")String storeId);



    @PostMapping(value = "sale/rob/upDateIsRobStats")
    @ApiOperation(value = "抢单", notes = "抢单")
    ServerResponse upDateIsRobStats(@RequestParam("request")HttpServletRequest request,
                                    @RequestParam("id")String id);


    @PostMapping(value = "sale/rob/queryCustomerInfo")
    @ApiOperation(value = "客户详情查询", notes = "客户详情查询")
    ServerResponse queryCustomerInfo(@RequestParam("request")HttpServletRequest request,
                                     @RequestParam("userToken")String userToken,
                                     @RequestParam("memberId")String memberId,
                                     @RequestParam("clueId")String clueId,
                                     @RequestParam("phaseStatus")Integer phaseStatus,
                                     @RequestParam("stage")String stage);

    @PostMapping(value = "sale/rob/addLabel")
    @ApiOperation(value = "新增标签", notes = "新增标签")
    ServerResponse addLabel(@RequestParam("request")HttpServletRequest request,
                            @RequestParam("memberId")String memberId,
                            @RequestParam("mcId")String mcId,
                            @RequestParam("labelId")String labelId,
                            @RequestParam("clueId")String clueId,
                            @RequestParam("phaseStatus")Integer phaseStatus);


    @PostMapping(value = "sale/rob/deleteLabel")
    @ApiOperation(value = "删除标签", notes = "删除标签")
    ServerResponse deleteLabel(@RequestParam("request")HttpServletRequest request,
                               @RequestParam("memberId")String memberId,
                               @RequestParam("labelIdArr")String labelIdArr,
                               @RequestParam("clueId")String clueId,
                               @RequestParam("phaseStatus")Integer phaseStatus);


    @PostMapping(value = "sale/rob/addDescribes")
    @ApiOperation(value = "新增沟通记录", notes = "新增沟通记录")
    ServerResponse addDescribes(@RequestParam("request")HttpServletRequest request,
                                @RequestBody CustomerRecDTO customerRecDTO,
                                @RequestParam("userToken")String userToken);

    @PostMapping(value = "sale/rob/remindTime")
    @ApiOperation(value = "定时执行", notes = "定时执行")
    void remindTime();


    @PostMapping(value = "sale/rob/upDateCustomerInfo")
    @ApiOperation(value = "修改客户信息", notes = "修改客户信息")
    ServerResponse upDateCustomerInfo(@RequestParam("request")HttpServletRequest request,
                                      @RequestBody Clue clue);

    @PostMapping(value = "sale/rob/addIntentionHouse")
    @ApiOperation(value = "新增意向房子", notes = "新增意向房子")
    ServerResponse addIntentionHouse(@RequestParam("request")HttpServletRequest request,
                                     @RequestBody IntentionHouse intentionHouse);


    @PostMapping(value = "sale/rob/deleteIntentionHouse")
    @ApiOperation(value = "删除意向房子", notes = "删除意向房子")
    ServerResponse deleteIntentionHouse(@RequestParam("request")HttpServletRequest request,
                                        @RequestParam("id")String id);
}
