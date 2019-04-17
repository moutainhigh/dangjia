package com.dangjia.acg.modle.core;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_house_construction_record")
@ApiModel(description = "施工记录表")
@FieldNameConstants(prefix = "")
public class HouseConstructionRecord extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房间id")
    @ApiModelProperty("房间id")
    private String houseId;

    @Column(name = "content")
    @Desc(value = "主体内容")
    @ApiModelProperty("主体内容")
    private String content;

    @Column(name = "worker_id")
    @Desc(value = "工人Id")
    @ApiModelProperty("工人Id")
    private String workerId;

    @Column(name = "work_type")
    @Desc(value = "工种类型")
    @ApiModelProperty("工种类型")
    private String workType;

    @Column(name = "apply_type")
    @Desc(value = "进度状态")
    @ApiModelProperty("进度状态")
    private Integer applyType;

    @Column(name = "house_flow_apply_id")
    @Desc(value = "排期工序表Id")
    @ApiModelProperty("排期工序表Id")
    private String houseFlowApplyId;

    @Column(name = "member_check")
    @Desc(value = "用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核")
    @ApiModelProperty("用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核")
    private Integer memberCheck;//membercheck

    @Column(name = "supervisor_check")
    @Desc(value = "大管家审核结果,0未审核，1审核通过，2审核不通过")
    @ApiModelProperty("大管家审核结果,0未审核，1审核通过，2审核不通过")
    private Integer supervisorCheck;//supervisorcheck

    @Column(name = "house_flow_id")
    @Desc(value = "进程ID")
    @ApiModelProperty("进程ID")
    private String houseFlowId;//houseflowid

}
