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

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/7/23
 * Time: 15:28
 */
@Data
@Entity
@Table(name = "dj_sale_residential_range")
@ApiModel(description = "外场销售小区范围")
@FieldNameConstants(prefix = "")
public class ResidentialRange extends BaseEntity {
    @Column(name = "village_id")
    @Desc(value = "小区id")
    @ApiModelProperty("小区id")
    private String villageId;

    @Column(name = "building_id")
    @Desc(value = "楼栋id字符串")
    @ApiModelProperty("楼栋id字符串d")
    private String buildingId;

    @Column(name = "user_id")
    @Desc(value = "销售id")
    @ApiModelProperty("销售id")
    private String userId;
}
