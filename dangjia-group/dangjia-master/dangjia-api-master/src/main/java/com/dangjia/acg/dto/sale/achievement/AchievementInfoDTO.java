package com.dangjia.acg.dto.sale.achievement;

import com.dangjia.acg.common.annotation.Desc;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 *门店业绩 返回参数
 */
@Data
@Entity
@ApiModel(description = "业绩返回参数")
@FieldNameConstants(prefix = "")
public class AchievementInfoDTO implements Serializable {

    @Column(name = "user_id")
    @Desc(value = "销售ID ")
    @ApiModelProperty("销售ID")
    private String userId;

    @Column(name = "store_id")
    @Desc(value = "店长ID ")
    @ApiModelProperty("店长ID")
    private String storeId;

    @ApiModelProperty("销售员工提成")
    private Integer userRoyalty;

    @Desc(value = "订单状态")
    @ApiModelProperty("订单状态")
    private Integer visitState;

    @ApiModelProperty("当月提成")
    private Integer monthRoyalty;

    @Column(name = "username")
    @ApiModelProperty("销售名称")
    private String username;

    @ApiModelProperty("下单数")
    private Integer singleNumber;

    @Desc(value = "目标数")
    @ApiModelProperty("目标数")
    private Integer targetNumber;

    @ApiModelProperty("累计提成")
    private Integer meterRoyalty;

    @ApiModelProperty("全部提成")
    private Integer arrRoyalty;

    @ApiModelProperty("销售人员订单条数")
    private Integer getSingleNumber;

    @ApiModelProperty("当条数据提成")
    private Integer ziduan;

    @ApiModelProperty("订单状态 0：新开工 1：已竣工")
    private Integer orderStatus;



    public String getVisitStateName() {
        if(null != getVisitState() && 1 == getVisitState()){
            return "装修中";
        }
        if(null != getVisitState() && 3 == getVisitState()){
            return "已完工";
        }
        return null;
    }
}
