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
import java.math.BigDecimal;

/**
 * @author Ruking.Cheng
 * @descrilbe 设计/精算业务单
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/27 11:55 AM
 */
@Data
@Entity
@Table(name = "dj_design_business_order")
@FieldNameConstants(prefix = "")
@ApiModel(description = "设计/精算业务单")
public class DesignBusinessOrder extends BaseEntity {

    @Column(name = "type")
    @Desc(value = "配置类型：1:平面图审核，2，施工图审核，3，精算修改费用，4:设计图施工过程中修改")
    @ApiModelProperty("配置类型：1:平面图审核，2，施工图审核，3，精算修改费用，4:设计图施工过程中修改")
    private Integer type;

    @Column(name = "sum_money")
    @Desc(value = "金额")
    @ApiModelProperty("金额")
    private BigDecimal sumMoney;

    @Column(name = "operation_state")
    @Desc(value = "操作状态：0:初始化或打回，1:发送业主，2:业主已确认")
    @ApiModelProperty("操作状态：0:初始化或打回，1:发送业主，2:业主已确认")
    private Integer operationState;

    @Column(name = "status")
    @Desc(value = "支付状态：0:未支付，1:已支付")
    @ApiModelProperty("支付状态：0:未支付，1:已支付")
    private Integer status;

    @Column(name = "frequency")
    @Desc(value = "打回次数")
    @ApiModelProperty("打回次数")
    private Integer frequency;

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

}
