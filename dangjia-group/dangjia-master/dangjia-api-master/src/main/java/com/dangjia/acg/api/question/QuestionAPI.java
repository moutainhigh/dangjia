package com.dangjia.acg.api.question;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ruking.Cheng
 * @descrilbe 排雷接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/10 8:40 PM
 */
@FeignClient("dangjia-service-master")
@Api(value = "排雷接口", description = "排雷接口")
public interface QuestionAPI {
    /**
     * showdoc
     *
     * @param questionId   可选 string 试题ID：为空时则新增否则为编辑
     * @param question     必选 string 试题题目名
     * @param questionType 必选 int 试题类型0:排雷（单选）
     * @param optionJson   必选 string 试题选项。格式为"[{"optionId":"选项ID：为空时则新增否则为编辑","content":"选项内容"},{"optionId":"","content":""}]"
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/设计模块/排雷模块
     * @title 新增/编辑试题
     * @description 新增/编辑试题
     * @method POST
     * @url master/member/question/setQuestion
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 1
     * @Author: Ruking 18075121944
     * @Date: 2019/12/12 10:44 AM
     */
    @PostMapping("member/question/setQuestion")
    @ApiOperation(value = "新增/编辑试题", notes = "新增/编辑试题")
    ServerResponse setQuestion(@RequestParam("request") HttpServletRequest request,
                               @RequestParam("questionId") String questionId,
                               @RequestParam("question") String question,
                               @RequestParam("questionType") int questionType,
                               @RequestParam("optionJson") String optionJson);

    /**
     * showdoc
     *
     * @param pageNum      必选 int 页码-1:全部
     * @param pageSize     必选 int 记录数
     * @param questionType 必选 int 试题类型0:排雷（单选）
     * @return {"res": 1000,"msg": {"resultCode": 1000, "resultMsg": "ok", "resultObj": { "pageNum": 0,"pageSize": 10,"size": 1,"startRow": 1,"endRow": 1,"total": 1, "pages": 1,"list": [{返回参数说明}],"prePage": 0, "nextPage": 1,"isFirstPage": false,"isLastPage": false,"hasPreviousPage": false,"hasNextPage": true,"navigatePages": 8,"navigatepageNums": [1],"navigateFirstPage": 1,"navigateLastPage": 1}}}
     * @catalog 工匠端升级/设计模块/排雷模块
     * @title 获取试题列表
     * @description 获取试题列表
     * @method POST
     * @url master/member/question/getQuestionList
     * @return_param id string 试题questionId
     * @return_param createDate string 创建时间
     * @return_param modifyDate string 修改时间
     * @return_param dataStatus int 数据状态:0=正常，1=删除
     * @return_param question string 题目
     * @return_param questionType int 试题类型0:排雷（单选）
     * @return_param optionList List<QuestionOption> 选项集合
     * @return_param optionList—id string 选项optionId
     * @return_param optionList—createDate string 选项创建时间
     * @return_param optionList—modifyDate string 选项修改时间
     * @return_param optionList—dataStatus int 选项数据状态:0=正常，1=删除
     * @return_param optionList—questionId string 试题questionId
     * @return_param optionList—content string 选项内容
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 2
     * @Author: Ruking 18075121944
     * @Date: 2019/12/12 10:51 AM
     */
    @PostMapping("member/question/getQuestionList")
    @ApiOperation(value = "获取试题列表", notes = "获取试题列表")
    ServerResponse getQuestionList(@RequestParam("request") HttpServletRequest request,
                                   @RequestParam("questionType") int questionType,
                                   @RequestParam("pageDTO") PageDTO pageDTO);

    /**
     * showdoc
     *
     * @param questionId 可选 string 试题ID
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/设计模块/排雷模块
     * @title 删除试题
     * @description 删除试题
     * @method POST
     * @url master/member/question/deleteQuestion
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 3
     * @Author: Ruking 18075121944
     * @Date: 2019/12/12 10:59 AM
     */
    @PostMapping("member/question/deleteQuestion")
    @ApiOperation(value = "删除试题", notes = "删除试题")
    ServerResponse deleteQuestion(@RequestParam("request") HttpServletRequest request,
                                  @RequestParam("questionId") String questionId);

    /**
     * showdoc
     *
     * @param houseId    必选 string 房子ID
     * @param questionJson 必选 string 试题和选项集合，格式为"[{"questionId":"试题ID","questionOptionId","选项ID"}]"
     * @return {"res":1000,"msg":{"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/设计模块/排雷模块
     * @title 设置房子排雷
     * @description 设置房子排雷
     * @method POST
     * @url master/member/question/setQuantityQuestion
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 4
     * @Author: Ruking 18075121944
     * @Date: 2019/12/12 1:53 PM
     */
    @PostMapping("member/question/setQuantityQuestion")
    @ApiOperation(value = "设置房子排雷", notes = "设置房子排雷")
    ServerResponse setQuantityQuestion(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("houseId") String houseId,
                                       @RequestParam("questionJson") String questionJson);

    /**
     * showdoc
     *
     * @param houseId 必选 string 房子ID
     * @return {"res":1000,"msg":{"resultObj":[{返回参数说明},{返回参数说明}],"resultCode":1000,"resultMsg":"成功"} }
     * @catalog 工匠端升级/设计模块/排雷模块
     * @title 获取房子排雷
     * @description 获取房子排雷
     * @method POST
     * @url master/member/question/getQuantityQuestion
     * @return_param questionId string 试题ID
     * @return_param question string 题目
     * @return_param questionOptionId string 选项ID
     * @return_param content string 选项内容
     * @remark 更多返回错误代码请看首页的错误代码描述
     * @number 5
     * @Author: Ruking 18075121944
     * @Date: 2019/12/12 3:15 PM
     */
    @PostMapping("member/question/getQuantityQuestion")
    @ApiOperation(value = "获取房子排雷", notes = "获取房子排雷")
    ServerResponse getQuantityQuestion(@RequestParam("request") HttpServletRequest request,
                                       @RequestParam("houseId") String houseId);
}
