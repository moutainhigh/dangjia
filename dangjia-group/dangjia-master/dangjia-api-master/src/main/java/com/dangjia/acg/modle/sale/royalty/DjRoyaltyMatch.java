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
@Table(name = "dj_royalty_match")
@ApiModel(description = "业绩表")
@FieldNameConstants(prefix = "")
public class DjRoyaltyMatch extends BaseEntity {

    @Column(name = "user_Id")
    @Desc(value = "销售Id")
    @ApiModelProperty("销售Id")
    private String userId;

    @Column(name = "house_id")
    @Desc(value = "房子Id")
    @ApiModelProperty("房子Id")
    private String houseId;

    @Column(name = "month_royalty")
    @Desc(value = "当月提成")
    @ApiModelProperty("当月提成")
    private Integer monthRoyalty;


    @Column(name = "meter_royalty")
    @Desc(value = "累计提成")
    @ApiModelProperty("累计提成")
    private Integer meterRoyalty;

    @Column(name = "arr_royalty")
    @Desc(value = "全部提成")
    @ApiModelProperty("全部提成")
    private Integer arrRoyalty;


}
