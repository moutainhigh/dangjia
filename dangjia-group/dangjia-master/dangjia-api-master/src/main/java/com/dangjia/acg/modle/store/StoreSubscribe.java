package com.dangjia.acg.modle.store;

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
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/14
 * Time: 17:58
 */
@Data
@Entity
@Table(name = "dj_store_subscribe")
@ApiModel(description = "门店预约表")
@FieldNameConstants(prefix = "")
public class StoreSubscribe extends BaseEntity {

    @Column(name = "store_id")
    @Desc(value = "门店id")
    @ApiModelProperty("门店id")
    private String storeId;

    @Column(name = "store_name")
    @Desc(value = "门店名称")
    @ApiModelProperty("门店名称")
    private String storeName;

    @Column(name = "customer_name")
    @Desc(value = "客户名称")
    @ApiModelProperty("客户名称")
    private String customerName;

    @Column(name = "customer_phone")
    @Desc(value = "客户电话")
    @ApiModelProperty("客户电话")
    private String customerPhone;


    @Column(name = "info")
    @Desc(value = "回访记录")
    @ApiModelProperty("回访记录")
    private String info;

    @Column(name = "state")
    @Desc(value = "回访状态")
    @ApiModelProperty("回访状态")
    private Integer state;

}
