package com.dangjia.acg.modle.question;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Ruking.Cheng
 * @descrilbe 试题选项表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/10 6:51 PM
 */
@Data
@Entity
@Table(name = "dj_question_option")
@ApiModel(description = "试题选项表")
@FieldNameConstants(prefix = "")
public class QuestionOption extends BaseEntity {


    @Column(name = "question_id")
    @Desc(value = "试题ID")
    @ApiModelProperty("试题ID")
    private String questionId;


    @Column(name = "content")
    @Desc(value = "选项内容")
    @ApiModelProperty("选项内容")
    private String content;
}
