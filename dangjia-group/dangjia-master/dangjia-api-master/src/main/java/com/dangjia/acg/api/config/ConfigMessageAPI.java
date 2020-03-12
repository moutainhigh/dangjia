package com.dangjia.acg.api.config;

import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.config.ConfigMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * author: qiyuxiang
 * Date: 2018/11/07
 * Time: 16:16
 */
@FeignClient("dangjia-service-master")
@Api(value = "公告消息接口", description = "公告消息接口")
public interface ConfigMessageAPI {
    /**
     * 获取所有公告消息
     *
     * @param configMessage
     * @return
     */
    @PostMapping("/config/message/list")
    @ApiOperation(value = "获取所有公告消息", notes = "获取所有公告消息")
    ServerResponse getConfigMessages(@RequestParam("request") HttpServletRequest request,
                                     @RequestParam("pageDTO") PageDTO pageDTO,
                                     @RequestParam("configMessage") ConfigMessage configMessage);

    /**
     * 获取所有公告消息
     *
     * @return
     */
    @PostMapping("/config/message/all")
    @ApiOperation(value = "获取所有公告消息(web端列表)", notes = "获取所有公告消息(web端列表)")
    ServerResponse queryConfigMessages(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * 新增公告消息
     *
     * @param configMessage
     * @return
     */
    @PostMapping("/config/message/add")
    @ApiOperation(value = "新增公告消息", notes = "新增公告消息")
    ServerResponse addConfigMessage(@RequestParam("request") HttpServletRequest request,
                                    @RequestParam("configMessage") ConfigMessage configMessage);



    @PostMapping("/config/message/addConfigMessage")
    @ApiOperation(value = "沟通提示消息", notes = "沟通提示消息")
    ServerResponse addConfigMessage(@RequestParam("memberId") String memberId,
                                    @RequestParam("title") String title,
                                    @RequestParam("alert") String alert,
                                    @RequestParam("type") Integer type,
                                    @RequestParam("data")String data);

    @PostMapping("/config/message/addConfigMessageServer")
    @ApiOperation(value = "消息推送--远程调用", notes = "远程调用")
    ServerResponse addConfigMessageServer(@RequestParam("appType") AppType appType,
                                    @RequestParam("memberId") String memberId,
                                    @RequestParam("targetType") String targetType,
                                    @RequestParam("title") String title,
                                    @RequestParam("alert") String alert,
                                    @RequestParam("type") Integer type,
                                    @RequestParam("data")String data,
                                    @RequestParam("speak")String speak);


    @PostMapping("/config/message/addRefundConfigMessage")
    @ApiOperation(value = "业主退货退款推送消息", notes = "业主退货退款推送消息")
    ServerResponse addRefundConfigMessage(@RequestParam("request") HttpServletRequest request,
                                          @RequestParam("appType") AppType appType,
                                          @RequestParam("memberId") String memberId,
                                          @RequestParam("targetType") String targetType,
                                          @RequestParam("title") String title,
                                          @RequestParam("alert") String alert,
                                          @RequestParam("typeText") String typeText);


}
