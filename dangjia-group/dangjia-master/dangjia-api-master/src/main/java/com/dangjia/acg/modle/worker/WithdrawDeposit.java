package com.dangjia.acg.modle.worker;

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
import java.util.Date;

/**
 * 实体类 - 提现申请
 *
 * @author ronalcheng
 */
@Data
@Entity
@Table(name = "dj_worker_withdraw_deposit")
@ApiModel(description = "提现申请")
@FieldNameConstants(prefix = "")
public class WithdrawDeposit extends BaseEntity {

    @Column(name = "bank_name")
    @Desc(value = "银行名字")
    @ApiModelProperty("银行名字")
    private String bankName;


    @Column(name = "card_number")
    @Desc(value = "卡号")
    @ApiModelProperty("卡号")
    private String cardNumber;


    @Column(name = "image")
    @Desc(value = "回执单图片")
    @ApiModelProperty("回执单图片")
    private String image;


    @Column(name = "memo")
    @Desc(value = "附言 可编辑")
    @ApiModelProperty("附言 可编辑")
    private String memo;


    @Column(name = "money")
    @Desc(value = "本次提现金额")
    @ApiModelProperty("本次提现金额")
    private BigDecimal money;


    @Column(name = "name")
    @Desc(value = "工匠姓名")
    @ApiModelProperty("工匠姓名")
    private String name;


    @Column(name = "processing_date")
    @Desc(value = "处理时间")
    @ApiModelProperty("处理时间")
    private Date processingDate;


    @Column(name = "reason")
    @Desc(value = "不同意理由")
    @ApiModelProperty("不同意理由")
    private String reason;


    @Column(name = "role_type")
    @Desc(value = "来源类型1：业主端  2 大管家 3：工匠端")
    @ApiModelProperty("来源类型1：业主端  2 大管家 3：工匠端")
    private Integer roleType;


    @Column(name = "state")
    @Desc(value = "0未处理,1同意 2不同意(驳回)")
    @ApiModelProperty("0未处理,1同意 2不同意(驳回)")
    private Integer state;


    @Column(name = "worker_id")
    @Desc(value = "工人id")
    @ApiModelProperty("工人id")
    private String workerId;


    @Column(name = "rate_money")
    @Desc(value = "手续费")
    @ApiModelProperty("手续费")
    private BigDecimal rateMoney;


    @Column(name = "apply_money")
    @Desc(value = "申请金额")
    @ApiModelProperty("申请金额")
    private BigDecimal applyMoney;
}














