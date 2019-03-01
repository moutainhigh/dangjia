package com.dangjia.acg.api.member;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Feedback;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * author: qiyuxiang
 * Date: 2018/11/09
 * Time: 11:00
 */
@FeignClient("dangjia-service-master")
@Api(value = "客服反馈接口", description = "客服反馈接口")
public interface FeedbackAPI {

    @PostMapping("/member/feedback/add")
    @ApiOperation(value = "添加客服反馈信息", notes = "添加客服反馈信息")
    ServerResponse addFeedback(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("feedback") Feedback feedback);


    @PostMapping("/member/feedback/list")
    @ApiOperation(value = "查询客服反馈列表", notes = "查询客服反馈列表")
    ServerResponse getFeedbacks(@RequestParam("request") HttpServletRequest request,
                                @RequestParam("pageDTO") PageDTO pageDTO,
                                @RequestParam("feedback") Feedback feedback);
}
