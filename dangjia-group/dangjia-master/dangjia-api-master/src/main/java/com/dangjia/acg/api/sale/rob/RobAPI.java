package com.dangjia.acg.api.sale.rob;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.clue.ClueTalkDTO;
import com.dangjia.acg.dto.sale.rob.CustomerRecDTO;
import com.dangjia.acg.modle.clue.Clue;
import com.dangjia.acg.modle.home.IntentionHouse;
import com.dangjia.acg.modle.sale.royalty.DjAlreadyRobSingle;
import com.dangjia.acg.modle.sale.royalty.DjRobSingle;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    @ApiOperation(value = "查询待抢单列表", notes = "查询待抢单列表")
    ServerResponse queryRobSingledata(@RequestParam("request")HttpServletRequest request,
                                      @RequestParam("userToken")String userToken,
                                      @RequestParam("storeId")String storeId,
                                      @RequestParam("isRobStats")Integer isRobStats);


    @PostMapping(value = "sale/rob/queryAlreadyRobSingledata")
    @ApiOperation(value = "查询已抢单列表", notes = "查询已抢单列表")
    ServerResponse queryAlreadyRobSingledata(@RequestParam("request")HttpServletRequest request,
                                             @RequestParam("userToken")String userToken,
                                             @RequestParam("request")String userId);


    @PostMapping(value = "sale/rob/upDateIsRobStats")
    @ApiOperation(value = "抢单", notes = "抢单")
    ServerResponse upDateIsRobStats(@RequestParam("request")HttpServletRequest request,
                                    @RequestBody DjAlreadyRobSingle djAlreadyRobSingle);


    @PostMapping(value = "sale/rob/upDateAlready")
    @ApiOperation(value = "放弃", notes = "放弃")
    ServerResponse upDateAlready(@RequestParam("request")HttpServletRequest request,
                                 @RequestParam("houseId")String houseId,
                                 @RequestParam("alreadyId")String alreadyId);


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
                            @RequestParam("mcId")String mcId,
                            @RequestParam("labelId")String labelId,
                            @RequestParam("clueId")String clueId,
                            @RequestParam("phaseStatus")Integer phaseStatus);


    @PostMapping(value = "sale/rob/deleteLabel")
    @ApiOperation(value = "删除标签", notes = "删除标签")
    ServerResponse deleteLabel(@RequestParam("request")HttpServletRequest request,
                               @RequestParam("mcId")String mcId,
                               @RequestParam("labelIdArr")String labelIdArr,
                               @RequestParam("clueId")String clueId,
                               @RequestParam("phaseStatus")Integer phaseStatus);


    @PostMapping(value = "sale/rob/addDescribes")
    @ApiOperation(value = "新增沟通记录", notes = "新增沟通记录")
    ServerResponse addDescribes(@RequestParam("request")HttpServletRequest request,
                                @RequestBody CustomerRecDTO customerRecDTO,
                                @RequestParam("userToken")String userToken);

    @PostMapping(value = "sale/rob/getTodayDescribes")
    @ApiOperation(value = "查询今天提醒的沟通记录", notes = "查询今天提醒的沟通记录")
    List<ClueTalkDTO> getTodayDescribes();


    @PostMapping(value = "sale/rob/remindTime")
    @ApiOperation(value = "定时执行", notes = "定时执行")
    void remindTime();


    @PostMapping(value = "sale/rob/upDateCustomerInfo")
    @ApiOperation(value = "修改客户信息", notes = "修改客户信息")
    ServerResponse upDateCustomerInfo(@RequestParam("request")HttpServletRequest request,
                                      @RequestBody Clue clue,
                                      @RequestParam("phaseStatus")Integer phaseStatus,
                                      @RequestParam("memberId")String memberId);

    @PostMapping(value = "sale/rob/addIntentionHouse")
    @ApiOperation(value = "新增意向房子", notes = "新增意向房子")
    ServerResponse addIntentionHouse(@RequestParam("request")HttpServletRequest request,
                                     @RequestBody IntentionHouse intentionHouse);


    @PostMapping(value = "sale/rob/deleteIntentionHouse")
    @ApiOperation(value = "删除意向房子", notes = "删除意向房子")
    ServerResponse deleteIntentionHouse(@RequestParam("request")HttpServletRequest request,
                                        @RequestParam("id")String id);


    @PostMapping(value = "sale/rob/addDjRobSingle")
    @ApiOperation(value = "新增抢单时间配置", notes = "新增抢单时间配置")
    ServerResponse addDjRobSingle(@RequestParam("request")HttpServletRequest request,
                                  @RequestParam("djRobSingle")DjRobSingle djRobSingle);

    @PostMapping(value = "sale/rob/upDateDjRobSingle")
    @ApiOperation(value = "修改抢单时间配置", notes = "修改抢单时间配置")
    ServerResponse upDateDjRobSingle(@RequestParam("request")HttpServletRequest request,
                                     @RequestParam("djRobSingle")DjRobSingle djRobSingle);

    @PostMapping(value = "sale/rob/deleteDjRobSingle")
    @ApiOperation(value = "删除抢单时间配置", notes = "删除抢单时间配置")
    ServerResponse deleteDjRobSingle(@RequestParam("request")HttpServletRequest request,
                                     @RequestParam("djRobSingle")DjRobSingle djRobSingle);

    @PostMapping(value = "sale/rob/queryDjRobSingle")
    @ApiOperation(value = "查询抢单时间配置", notes = "查询抢单时间配置")
    ServerResponse queryDjRobSingle(@RequestParam("request")HttpServletRequest request,
                                    @RequestParam("pageDTO")PageDTO pageDTO);


    @PostMapping(value = "sale/rob/notEnteredGrabSheet")
    @ApiOperation(value = "未录入抢单", notes = "未录入抢单")
    void notEnteredGrabSheet();

}
