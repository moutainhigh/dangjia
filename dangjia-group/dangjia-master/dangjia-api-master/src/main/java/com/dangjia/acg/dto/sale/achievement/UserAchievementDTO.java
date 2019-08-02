package com.dangjia.acg.dto.sale.achievement;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 *员工业绩 返回参数
 */
@Data
@Entity
@ApiModel(description = "员工业绩")
@FieldNameConstants(prefix = "")
public class UserAchievementDTO {

    @Column(name = "销售id")
    protected String userId;

    @Column(name = "真是姓名")
    protected String name;

    @Column(name = "头像")
    protected String head;

    @Column(name = "状态")
    protected Integer visitState;

    @Column(name = "昵称")
    protected String nickName;

    @ApiModelProperty("当月提成")
    private Integer monthRoyaltys;

    @ApiModelProperty("累计提成")
    private Integer meterRoyaltys;

    @ApiModelProperty("全部提成")
    private Integer arrRoyalty;




}
