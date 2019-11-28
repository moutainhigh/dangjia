package com.dangjia.acg.modle.house;

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
 * Date: 2018/12/13 0013
 * Time: 14:21
 */
@Data
@Entity
@Table(name = "dj_house_house_expend")
@ApiModel(description = "房子花费")
@FieldNameConstants(prefix = "")
public class HouseExpend extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "tol_money")
    @Desc(value = "总金额")
    @ApiModelProperty("总金额")
    private Double tolMoney;

    @Column(name = "pay_money")
    @Desc(value = "总支付金额")
    @ApiModelProperty("总支付金额")
    private Double payMoney;

    @Column(name = "dis_money")
    @Desc(value = "总优惠金额")
    @ApiModelProperty("总优惠金额")
    private Double disMoney;

    @Column(name = "back_money")
    @Desc(value = "总退款")
    @ApiModelProperty("总退款")
    private Double backMoney;

    @Column(name = "material_money")
    @Desc(value = "材料总花费")
    @ApiModelProperty("材料总花费")
    private Double materialMoney;

    @Column(name = "worker_money")
    @Desc(value = "人工总花费")
    @ApiModelProperty("人工总花费")
    private Double workerMoney;

    @Column(name = "material_kind")
    @Desc(value = "材料种类")
    @ApiModelProperty("材料种类")
    private Integer materialKind;

    public HouseExpend(){

    }

    public HouseExpend(Boolean isInit){
        if(isInit){
            this.tolMoney = 0.0;
            this.payMoney = 0.0;
            this.disMoney = 0.0;
            this.backMoney = 0.0;
            this.materialMoney = 0.0;
            this.workerMoney = 0.0;
            this.materialKind = 0;
        }
    }
}
