package com.dangjia.acg.api.feedback;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @description ljl
 */
@FeignClient("dangjia-service-master")
@Api(value = "用户反馈", description = "用户反馈")
public interface UserFeedbackAPI {

    @PostMapping("app/feedback/addFeedbackInFo")
    @ApiOperation(value = "添加反馈信息", notes = "添加反馈信息")
    ServerResponse addFeedbackInFo(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("userToken") String userToken,
            @RequestParam("userId") String userId,
            @RequestParam("appType") Integer appType,
            @RequestParam("image") String image,
            @RequestParam("remark") String remark);

    @PostMapping("web/feedback/queryFeedbackInFo")
    @ApiOperation(value = "查询反馈信息", notes = "查询反馈信息")
    ServerResponse queryFeedbackInFo(
                                @RequestParam("request") HttpServletRequest request,
                                @RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("appType")Integer appType,
                                @RequestParam("feedbackType")Integer feedbackType,
                                @RequestParam("beginDate")String beginDate,
                                @RequestParam("endDate") String endDate);


    @PostMapping("web/feedback/queryFeedbackItemInFo")
    @ApiOperation(value = "查询反馈详情信息", notes = "查询反馈详情信息")
    ServerResponse queryFeedbackItemInFo(
            @RequestParam("request") HttpServletRequest request,
            @RequestParam("id")String id,
            @RequestParam("feedbackId")String feedbackId);

    @GetMapping("web/feedback/exportFeedbackInFo")
    @ApiOperation(value = "导出反馈信息", notes = "导出反馈信息",produces = "*/*,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/octet-stream")
    ServerResponse exportFeedbackInFo(
            @RequestParam("request") HttpServletResponse request,
            @RequestParam("appType")Integer appType,
            @RequestParam("feedbackType")Integer feedbackType,
            @RequestParam("beginDate")String beginDate,
            @RequestParam("endDate")String endDate);


}
