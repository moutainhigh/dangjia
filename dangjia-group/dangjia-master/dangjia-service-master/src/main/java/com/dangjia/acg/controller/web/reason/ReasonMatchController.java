package com.dangjia.acg.controller.web.reason;

import com.dangjia.acg.api.feedback.UserFeedbackAPI;
import com.dangjia.acg.api.web.reason.ReasonMatchAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.feedback.UserFeedbackService;
import com.dangjia.acg.service.reason.ReasonMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ljl
 * @descrilbe 工匠更换原因控制层
 */
@RestController
public class ReasonMatchController implements ReasonMatchAPI {

    @Autowired
    private ReasonMatchService reasonMatchService;


    @Override
    @ApiMethod
    public ServerResponse addReasonInFo(HttpServletRequest request,String remark) {
        return reasonMatchService.addReasonInFo(remark);
    }

    @Override
    @ApiMethod
    public ServerResponse queryReasonInFo(HttpServletRequest request) {
        return reasonMatchService.queryReasonInFo();
    }

    @Override
    @ApiMethod
    public ServerResponse deleteReasonInFo(HttpServletRequest request,String id) {
        return reasonMatchService.deleteReasonInFo(id);
    }

    @Override
    @ApiMethod
    public ServerResponse upDateReasonInFo(HttpServletRequest request,String id,String remark) {
        return reasonMatchService.upDateReasonInFo(id,remark);
    }

}
