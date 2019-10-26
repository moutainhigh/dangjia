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
 * 实体类 - 房子备注
 */
@Data
@Entity
@Table(name = "dj_house_remark")
@ApiModel(description = "房子备注")
@FieldNameConstants(prefix = "")
public class HouseRemark extends BaseEntity {

    @Column(name = "remark_info")
    @Desc(value = "备注详情")
    @ApiModelProperty("备注详情")
    private String remarkInfo;

    @Column(name = "remark_name")
    @Desc(value = "备注人")
    @ApiModelProperty("备注人")
    private String remarkName;

    @Column(name = "client")
    @Desc(value = "客户端")
    @ApiModelProperty("客户端")
    private String client;

    @Column(name = "remark_type")
    @Desc(value = "备注类型：0-设计备注 1-精算备注")
    @ApiModelProperty("备注类型：0-设计备注 1-精算备注")
    private String remarkType;

    @Column(name = "house_id")
    @Desc(value = "房子id")
    @ApiModelProperty("房子id")
    private String houseId;


}
