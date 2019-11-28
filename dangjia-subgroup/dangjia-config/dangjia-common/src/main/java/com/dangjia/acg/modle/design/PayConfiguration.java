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
 * @descrilbe 支付配置表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/27 11:55 AM
 */
@Data
@Entity
@Table(name = "dj_pay_configuration")
@FieldNameConstants(prefix = "")
@ApiModel(description = "支付配置表")
public class PayConfiguration extends BaseEntity {

    @Column(name = "type")
    @Desc(value = "配置类型：1:平面图审核，2，施工图审核，3，精算修改费用，4:设计图施工过程中修改")
    @ApiModelProperty("配置类型：1:平面图审核，2，施工图审核，3，精算修改费用，4:设计图施工过程中修改")
    private Integer type;

    @Column(name = "sum_money")
    @Desc(value = "金额")
    @ApiModelProperty("金额")
    private BigDecimal sumMoney;

    @Column(name = "frequency")
    @Desc(value = "次数")
    @ApiModelProperty("次数")
    private Integer frequency;

    @Column(name = "name")
    @Desc(value = "条目名称")
    @ApiModelProperty("条目名称")
    private String name;


}
