package com.dangjia.acg.mapper.question;

import com.dangjia.acg.modle.question.Question;
import com.dangjia.acg.modle.question.QuestionOption;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Ruking.Cheng
 * @descrilbe 试题表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/12 9:48 AM
 */
@Repository
public interface IQuestionMapper extends Mapper<Question> {
}
