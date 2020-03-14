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
 * @descrilbe 排雷答案记录表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/10 6:52 PM
 */
@Data
@Entity
@Table(name = "dj_quantity_question")
@ApiModel(description = "排雷答案记录表")
@FieldNameConstants(prefix = "")
public class QuantityQuestion extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;


    @Column(name = "question_id")
    @Desc(value = "试题ID")
    @ApiModelProperty("试题ID")
    private String questionId;


    @Column(name = "question_option_id")
    @Desc(value = "选项ID")
    @ApiModelProperty("选项ID")
    private String questionOptionId;
}
