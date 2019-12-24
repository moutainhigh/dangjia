package com.dangjia.acg.controller.question;

import com.dangjia.acg.api.question.QuestionAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 排雷接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/12 10:40 AM
 */
@RestController
public class QuestionController implements QuestionAPI {
    @Autowired
    private QuestionService questionService;

    @Override
    @ApiMethod
    public ServerResponse setQuestion(HttpServletRequest request, String questionId, String question, Integer questionType, String optionJson) {
        return questionService.setQuestion(questionId, question, questionType, optionJson);
    }

    @Override
    @ApiMethod
    public ServerResponse getQuestionList(HttpServletRequest request, Integer questionType, PageDTO pageDTO) {
        return questionService.getQuestionList(questionType, pageDTO);
    }

    @Override
    @ApiMethod
    public ServerResponse deleteQuestion(HttpServletRequest request, String questionId) {
        return questionService.deleteQuestion(questionId);
    }

    @Override
    @ApiMethod
    public ServerResponse setQuantityQuestion(HttpServletRequest request, String houseId, String questionJson) {
        return questionService.setQuantityQuestion(houseId, questionJson);
    }

    @Override
    @ApiMethod
    public ServerResponse getQuantityQuestion(HttpServletRequest request, String houseId) {
        return questionService.getQuantityQuestion(houseId);
    }
}
