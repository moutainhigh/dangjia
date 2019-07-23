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

@Data
@Entity
@Table(name = "dj_store_users")
@ApiModel(description = "门店成员表")
@FieldNameConstants(prefix = "")
public class StoreUser extends BaseEntity {

    @Column(name = "user_id")
    @Desc(value = "成员ID")
    @ApiModelProperty("成员ID")
    private String userId;

    @Column(name = "store_id")
    @Desc(value = "门店ID")
    @ApiModelProperty("门店ID")
    private String storeId;

    @Column(name = "type")
    @Desc(value = "类别：0:场内销售，1:场外销售")
    @ApiModelProperty("类别：0:场内销售，1:场外销售")
    private Integer type;


}
