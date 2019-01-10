package com.dangjia.acg.modle.repair;

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
 * author: Ronalcheng
 * Date: 2019/1/7 0007
 * Time: 17:16
 * 实体类 变更单
 */
@Data
@Entity
@Table(name = "dj_repair_change_order")
@ApiModel(description = "申请变更单")
@FieldNameConstants(prefix = "")
public class ChangeOrder extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房子id")
    @ApiModelProperty("房子id")
    private String houseId;

    @Column(name = "member_id")
    @Desc(value = "业主id")
    @ApiModelProperty("业主id")
    private String memberId;

    @Column(name = "worker_id")
    @Desc(value = "工匠id")
    @ApiModelProperty("工匠id")
    private String workerId;

    @Column(name = "type")
    @Desc(value = "类型,1补人工,2退人工")
    @ApiModelProperty("类型")
    private Integer type;

    @Column(name = "content_a")
    @Desc(value = "描叙")
    @ApiModelProperty("描叙")
    private String contentA;

    @Column(name = "content_b")
    @Desc(value = "描叙")
    @ApiModelProperty("描叙")
    private String contentB;

    @Column(name = "member_check")
    @Desc(value = "用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核")
    @ApiModelProperty("用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核")
    private Integer memberCheck;

    @Column(name = "worker_check")
    @Desc(value = "工匠审核结果,0未审核，1审核通过，2审核不通过")
    @ApiModelProperty("工匠审核结果,0未审核，1审核通过，2审核不通过")
    private Integer workerCheck;


}
