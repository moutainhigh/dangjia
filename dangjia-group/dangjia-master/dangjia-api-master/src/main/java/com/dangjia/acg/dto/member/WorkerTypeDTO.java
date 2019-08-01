package com.dangjia.acg.dto.member;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 大管家信息
 */
@Data
@ApiModel
public class WorkerTypeDTO {

    @ApiModelProperty("大管家姓名")
    private String name;

    @ApiModelProperty("手机号码")
    private String mobile;

    @ApiModelProperty("图片")
    private String head;

    @ApiModelProperty("大管家姓名")
    private Integer visitState;
}
