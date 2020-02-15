package com.dangjia.acg.modle.account;

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
@Table(name = "dj_account_flow_record")
@ApiModel(description = "账户流水表（店铺，供应商）")
@FieldNameConstants(prefix = "")
public class AccountFlowRecord extends BaseEntity {

    @Column(name = "flow_type")
    @Desc(value = "类型:（1店铺，2供应商）")
    @ApiModelProperty("类型:（1店铺，2供应商）")
    private String flowType;//

    @Column(name = "house_id")
    @Desc(value = "房子id")
    @ApiModelProperty("房子id")
    private String houseId;//houseid

    @Column(name = "money")
    @Desc(value = "本次金额")
    @ApiModelProperty("本次金额")
    private Double money;//实拿

    @Column(name = "state")
    @Desc(value = "0订单收入,1提现,2自定义增加金额,3自定义减少金额 4:充值 5:交纳滞留金,6质保金变动")
    @ApiModelProperty("0订单收入,1提现,2自定义增加金额,3自定义减少金额 4:充值 5:交纳滞留金，6质保金变动")
    private Integer state;

    @Column(name = "defined_account_id")
    @Desc(value = "自定义账户流水id")
    @ApiModelProperty("自定义账户流水id")
    private String definedAccountId;

    /*新增字段*/
    @Column(name = "defined_name")
    @Desc(value = "自定义流水说明")
    @ApiModelProperty("自定义流水说明")
    private String definedName;

    @Column(name = "house_order_id")
    @Desc(value = "房子订单")
    @ApiModelProperty("房子订单")
    private String houseOrderId;

    @Column(name = "amount_after_money")
    @Desc(value = "入账后金额")
    @ApiModelProperty("入账后金额")
    private Double amountAfterMoney;//应拿

    @Column(name = "amount_before_money")
    @Desc(value = "入账前金额")
    @ApiModelProperty("入账前金额")
    private Double amountBeforeMoney;//对应member表 haveMoney

    @Column(name = "create_by")
    @Desc(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    @Column(name = "update_by")
    @Desc(value = "修改人")
    @ApiModelProperty("修改人")
    private String updateBy;
}
