package com.dangjia.acg.modle.sale.royalty;

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
@Table(name = "dj_order_surface")
@ApiModel(description = "记录门店抢单信息")
@FieldNameConstants(prefix = "")
public class DjOrderSurface extends BaseEntity {


    @Column(name = "clue_id")
    @Desc(value = "线索Id")
    @ApiModelProperty("线索Id")
    private String clueId;

    @Column(name = "store_id")
    @Desc(value = "门店Id")
    @ApiModelProperty("门店Id")
    private String storeId;

    @Column(name = "rob_date_id")
    @Desc(value = "时间id")
    @ApiModelProperty("时间id")
    private String robDateId;

}
