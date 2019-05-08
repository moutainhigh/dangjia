package com.dangjia.acg.modle.design;

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
 * @descrilbe 设计相关操作记录表（目前就只有量房）
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/27 11:55 AM
 */
@Data
@Entity
@Table(name = "dj_design_quantity_room")
@FieldNameConstants(prefix = "")
@ApiModel(description = "设计相关操作记录表（目前就只有量房）")
public class QuantityRoom extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "member_id")
    @Desc(value = "处理人的用户ID")
    @ApiModelProperty("处理人的用户ID")
    private String memberId;

    @Column(name = "user_id")
    @Desc(value = "中台处理人的用户ID")
    @ApiModelProperty("中台处理人的用户ID")
    private String userId;

    @Column(name = "type")
    @Desc(value = "事务类型：0:量房，1平面图，2施工图")
    @ApiModelProperty("事务类型：0:量房，1平面图，2施工图")
    private Integer type;//

    @Column(name = "operation_type")
    @Desc(value = "操作类型：0:执行，1：跳过")
    @ApiModelProperty("操作类型：0:执行，1：跳过")
    private Integer operationType;//

}
