package com.dangjia.acg.controller.member;

import com.dangjia.acg.api.member.FeedbackAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.member.Feedback;
import com.dangjia.acg.service.member.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author: Ronalcheng
 * Date: 2018/11/8 0008
 * Time: 11:27
 */
@RestController
public class FeedbackController implements FeedbackAPI {

    @Autowired
    private FeedbackService feedbackService;

    @Override
    @ApiMethod
    public ServerResponse addFeedback(HttpServletRequest request, Feedback feedback, Integer userRole) {
        return feedbackService.addFeedback(request, feedback, userRole);
    }

    @Override
    @ApiMethod
    public ServerResponse getFeedbacks(HttpServletRequest request, PageDTO pageDTO, Feedback feedback) {
        return feedbackService.getFeedbacks(request, pageDTO, feedback);
    }
}
