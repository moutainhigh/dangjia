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
 * @descrilbe 试题表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/10 6:50 PM
 */
@Data
@Entity
@Table(name = "dj_question")
@ApiModel(description = "试题表")
@FieldNameConstants(prefix = "")
public class Question extends BaseEntity {


    @Column(name = "question")
    @Desc(value = "题目")
    @ApiModelProperty("题目")
    private String question;


    @Column(name = "question_type")
    @Desc(value = "试题类型0:排雷（单选）")
    @ApiModelProperty("试题类型0:排雷（单选）")
    private Integer questionType;
}
