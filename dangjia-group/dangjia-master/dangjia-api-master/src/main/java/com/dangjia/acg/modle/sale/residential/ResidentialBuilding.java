package com.dangjia.acg.modle.sale.residential;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/23
 * Time: 15:24
 */
@Data
@Entity
@Table(name = "dj_sale_residential_building")
@ApiModel(description = "小区楼栋")
@FieldNameConstants(prefix = "")
public class ResidentialBuilding extends BaseEntity {

    @Column(name = "village_id")
    @Desc(value = "小区id")
    @ApiModelProperty("小区id")
    private String villageId;

    @Column(name = "building")
    @Desc(value = "楼栋名称")
    @ApiModelProperty("楼栋名称")
    private String building;

    @Column(name = "store_id")
    @Desc(value = "门店id")
    @ApiModelProperty("门店id")
    private String storeId;

    @Transient
    private String Checked="0";//是否被选中 1:被选中 0：未被选中
}
