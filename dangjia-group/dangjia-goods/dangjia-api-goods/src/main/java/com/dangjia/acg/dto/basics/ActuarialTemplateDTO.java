package com.dangjia.acg.dto.basics;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 返回精算模版model
 *
 * @类 名： ActuarialTemplateResult
 * @功能描述：
 * @作者信息： lxl
 * @创建时间： 2018-9-21上午13:35:10
 */
@Data
@ApiModel
public class ActuarialTemplateDTO {
    @ApiModelProperty("id")
    private String id;//精算模板id
    @ApiModelProperty("userId")
    private String userId;//用户ID
    @ApiModelProperty("name")
    private String name;//精算模板名称
    @ApiModelProperty("workingProcedure")
    private String workingProcedure;//工序
    @ApiModelProperty("workerTypeId")
    private String workerTypeId;
    @ApiModelProperty("styleType")
    private String styleType;//风格
    @ApiModelProperty("applicableArea")
    private String applicableArea;//适用面积
    @ApiModelProperty("numberOfUse")
    private Integer numberOfUse;//使用次数
    @ApiModelProperty("stateType")
    private Integer stateType;//状态0为停用1为启用
}

