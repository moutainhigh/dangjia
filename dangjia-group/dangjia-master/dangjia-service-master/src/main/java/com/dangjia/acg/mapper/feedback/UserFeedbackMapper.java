package com.dangjia.acg.mapper.feedback;

import com.dangjia.acg.dto.feedback.UserFeedbackDTO;
import com.dangjia.acg.modle.feedback.UserFeedback;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 用户反馈mapper
 */
@Repository
public interface UserFeedbackMapper extends Mapper<UserFeedback> {

    List<UserFeedbackDTO> queryFeedbackInFo(@Param("appType")Integer appType,
                                            @Param("feedbackType")Integer feedbackType,
                                            @Param("beginDate")String beginDate,
                                            @Param("endDate")String endDate);

}
