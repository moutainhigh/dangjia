package com.dangjia.acg.modle.operation;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Ruking.Cheng
 * @descrilbe 施工操作流水
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/11 8:33 PM
 */
@Data
@Entity
@Table(name = "dj_operation_flow")
@ApiModel(description = "施工操作流水")
@FieldNameConstants(prefix = "")
public class OperationFlow extends BaseEntity {


    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;


    @Column(name = "worker_type")
    @Desc(value = "工种类别1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
    @ApiModelProperty("工种类别1设计师，2精算师，3大管家,4拆除，6水电工，7防水，8泥工,9木工，10油漆工")
    private Integer workerType;


    @Column(name = "operation_type")
    @Desc(value = "操作类型（自定义）")
    @ApiModelProperty("操作类型（自定义）")
    private String operationType ;

    @Column(name = "operation_id")
    @Desc(value = "操作类型对应业务ID")
    @ApiModelProperty("操作类型对应业务ID")
    private String operationId ;

    @Column(name = "name")
    @Desc(value = "操作名称")
    @ApiModelProperty("操作名称")
    private String name;


    @Column(name = "remarks")
    @Desc(value = "操作描述")
    @ApiModelProperty("操作描述")
    private String remarks;


    @Column(name = "user_type")
    @Desc(value = "操作用户类型0:中台，1:APP")
    @ApiModelProperty("操作用户类型0:中台，1:APP")
    private Integer userType;


    @Column(name = "user_id")
    @Desc(value = "操作用户ID")
    @ApiModelProperty("操作用户ID")
    private String userId;


    @Transient
    private String userName;
}
