package com.dangjia.acg.dto.engineer;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class ResponsiblePartyDetailDTO {
    private Date createDate;//质保申请日期
    private String address;// 地址
    private double totalAmount;//维保分摊总额
    private String proportion;//维保责任方占比
    private String workerTypeId;//工匠类型
    private String workerTypeName;//工种类型名称
    private String content;//缴纳描述
    private Double needRetentionMoney;//所需质保金
    private Double retentionMoney;//现有质保金
    private Double paidRetentionMoney;//需要缴纳质保金额
    private String houseId;//房子id

    private String responsiblePartyId;//责任划分ID
    private String titleName;//顶部描述
    private Date responsiblePartyDate;//责任划分时间
    private Date endResponsiblePartyDate;//截止可申诉时间
    private Integer complainStatus;//申诉状态(-1 未申诉，0申诉中，1已驳回，2已处理）
    private String complainId;//申诉ID
    private String complainReason;//驳回原因
    private String complainImage;//申诉图片
    private String complainImageUrl;//申诉图片地址
    private String complainContent;//申诉内容
    private Integer isComplain;//是否可申诉 （1是，0否)
    private Double amountAfterMoney;//入帐后金额
    private Double amountBeforeMoney;//入帐前金额
    private Double money;//变动金额

}
