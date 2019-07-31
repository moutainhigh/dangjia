package com.dangjia.acg.dto.member;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 沟通记录
 */
@Data
public class CustomerRecordInFoDTO implements Serializable {

    @ApiModelProperty("业主id")
    private String memberId;

    @ApiModelProperty("沟通记录")
    private String describes;

    @ApiModelProperty("提醒时间")
    private Date remindTime;

    @ApiModelProperty("创建时间")
    private Date createDate;

    @ApiModelProperty("头像")
    private String head;

}
