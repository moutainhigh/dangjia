package com.dangjia.acg.service.question;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.question.IQuantityQuestionMapper;
import com.dangjia.acg.mapper.question.IQuestionMapper;
import com.dangjia.acg.mapper.question.IQuestionOptionMapper;
import com.dangjia.acg.modle.question.QuantityQuestion;
import com.dangjia.acg.modle.question.Question;
import com.dangjia.acg.modle.question.QuestionOption;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe 排雷接口实现
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/12 9:55 AM
 */
@Service
public class QuestionService {
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IQuantityQuestionMapper iQuantityQuestionMapper;
    @Autowired
    private IQuestionMapper iQuestionMapper;
    @Autowired
    private IQuestionOptionMapper iQuestionOptionMapper;

    /**
     * 新增/编辑试题
     *
     * @param questionId   可选 string 试题ID：为空时则新增否则为编辑
     * @param question     必选 string 试题题目名
     * @param questionType 必选 int 试题类型0:排雷（单选）
     * @param optionJson   必选 string 试题选项。格式为"[{"optionId":"选项ID：为空时则新增否则为编辑","content":"选项内容"},{"optionId":"","content":""}]"
     * @return ServerResponse
     */
    public ServerResponse setQuestion(String questionId, String question, int questionType, String optionJson) {
        if (CommonUtil.isEmpty(question)) {
            return ServerResponse.createByErrorMessage("题目名称不能为空");
        }
        if (CommonUtil.isEmpty(optionJson)) {
            return ServerResponse.createByErrorMessage("题目名称不能为空");
        }
        boolean isUpdata = false;
        Question question1;
        if (CommonUtil.isEmpty(questionId)) {
            question1 = new Question();
        } else {
            question1 = iQuestionMapper.selectByPrimaryKey(questionId);
            if (question1 == null) {
                question1 = new Question();
            } else {
                question1.setModifyDate(new Date());
                isUpdata = true;
            }
        }
        question1.setQuestion(question);
        question1.setQuestionType(questionType);
        JSONArray optionList = JSONArray.parseArray(optionJson);
        if (optionList != null && optionList.size() > 1) {
            Example example = new Example(QuestionOption.class);
            example.createCriteria().andEqualTo(QuestionOption.QUESTION_ID, question1.getId())
                    .andEqualTo(QuestionOption.DATA_STATUS, 0);
            QuestionOption questionOption = new QuestionOption();
            questionOption.setId(null);
            questionOption.setCreateDate(null);
            questionOption.setDataStatus(1);
            iQuestionOptionMapper.updateByExampleSelective(questionOption, example);
            for (Object anOptionList : optionList) {
                JSONObject obj = (JSONObject) anOptionList;
                String optionId = obj.getString("optionId");
                String content = obj.getString("content");
                boolean isUpdataOption = false;
                QuestionOption option;
                if (CommonUtil.isEmpty(optionId)) {
                    option = new QuestionOption();
                } else {
                    option = iQuestionOptionMapper.selectByPrimaryKey(optionId);
                    if (option == null) {
                        option = new QuestionOption();
                    } else {
                        option.setModifyDate(new Date());
                        isUpdataOption = true;
                    }
                }
                option.setQuestionId(question1.getId());
                option.setContent(content);
                option.setDataStatus(0);
                if (isUpdataOption) {
                    iQuestionOptionMapper.updateByPrimaryKeySelective(option);
                } else {
                    iQuestionOptionMapper.insertSelective(option);
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("题目选项必须大于2");
        }
        if (isUpdata) {
            iQuestionMapper.updateByPrimaryKeySelective(question1);
            return ServerResponse.createBySuccessMessage("修改成功");
        } else {
            iQuestionMapper.insertSelective(question1);
            return ServerResponse.createBySuccessMessage("新增成功");
        }
    }

    /**
     * 获取试题列表
     *
     * @param questionType 试题类型0:排雷（单选）
     * @param pageDTO      pageDTO
     * @return ServerResponse
     */
    public ServerResponse getQuestionList(int questionType, PageDTO pageDTO) {
        if (pageDTO.getPageNum() != -1)
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(Question.class);
        Example.Criteria criteria = example.createCriteria();
        if (questionType != -1)
            criteria.andEqualTo(Question.QUESTION_TYPE, questionType);
        criteria.andEqualTo(Question.DATA_STATUS, 0);
        example.orderBy(Question.CREATE_DATE).desc();
        List<Question> questions = iQuestionMapper.selectByExample(example);
        if (questions.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "暂无试题");
        }
        PageInfo pageResult = new PageInfo(questions);
        List<Map> maps = (List<Map>) BeanUtils.listToMap(questions);
        for (Map map : maps) {
            example = new Example(QuestionOption.class);
            criteria = example.createCriteria();
            criteria.andEqualTo(QuestionOption.QUESTION_ID, map.get(Question.ID));
            criteria.andEqualTo(QuestionOption.DATA_STATUS, 0);
            example.orderBy(QuestionOption.CREATE_DATE).desc();
            List<QuestionOption> optionList = iQuestionOptionMapper.selectByExample(example);
            map.put("optionList", optionList);
        }
        pageResult.setList(maps);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 删除试题
     *
     * @param questionId 可选 string 试题ID
     * @return ServerResponse
     */
    public ServerResponse deleteQuestion(String questionId) {
        Question question = iQuestionMapper.selectByPrimaryKey(questionId);
        if (question == null) {
            return ServerResponse.createByErrorMessage("未找到该试题");
        }
        question.setModifyDate(new Date());
        question.setDataStatus(1);
        iQuestionMapper.updateByPrimaryKeySelective(question);
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    public ServerResponse setQuantityQuestion(String houseId, String questionJson) {
        if (CommonUtil.isEmpty(houseId)) {
            return ServerResponse.createByErrorMessage("未选择房子");
        }
        if (CommonUtil.isEmpty(questionJson)) {
            return ServerResponse.createByErrorMessage("试题不能为空");
        }
        JSONArray questionList = JSONArray.parseArray(questionJson);
        if (questionList != null && questionList.size() > 1) {
            Example example = new Example(QuantityQuestion.class);
            example.createCriteria().andEqualTo(QuantityQuestion.HOUSE_ID, houseId)
                    .andEqualTo(QuantityQuestion.DATA_STATUS, 0);
            QuantityQuestion quantityQuestion = new QuantityQuestion();
            quantityQuestion.setId(null);
            quantityQuestion.setCreateDate(null);
            quantityQuestion.setDataStatus(1);
            iQuantityQuestionMapper.updateByExampleSelective(quantityQuestion, example);
            for (Object o : questionList) {
                JSONObject obj = (JSONObject) o;
                String questionId = obj.getString("questionId");
                String questionOptionId = obj.getString("questionOptionId");
                QuantityQuestion question = new QuantityQuestion();
                question.setHouseId(houseId);
                question.setQuestionId(questionId);
                question.setQuestionOptionId(questionOptionId);
                iQuantityQuestionMapper.insertSelective(question);
            }
        } else {
            return ServerResponse.createByErrorMessage("试题不能为空");
        }
        return ServerResponse.createBySuccessMessage("设置成功");
    }

    public ServerResponse getQuantityQuestion(String houseId) {
        Example example = new Example(QuantityQuestion.class);
        example.createCriteria().andEqualTo(QuantityQuestion.HOUSE_ID, houseId)
                .andEqualTo(QuantityQuestion.DATA_STATUS, 0);
        List<QuantityQuestion> questions = iQuantityQuestionMapper.selectByExample(example);
        if (questions.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "暂无数据");
        }
        List<Map> maps = (List<Map>) BeanUtils.listToMap(questions);
        for (Map map : maps) {
            Question question = iQuestionMapper.selectByPrimaryKey(map.get(QuantityQuestion.QUESTION_ID));
            if (question != null) {
                map.put("question", question.getQuestion());
            }
            QuestionOption questionOption = iQuestionOptionMapper.selectByPrimaryKey(map.get(QuantityQuestion.QUESTION_OPTION_ID));
            if (questionOption != null) {
                map.put("content", questionOption.getContent());
            }
        }
        return ServerResponse.createBySuccess("查询成功", maps);
    }
}
