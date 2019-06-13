package com.dangjia.acg.modle.home;

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
 * @author Ruking.Cheng
 * @descrilbe 首页配置表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/13 10:59 AM
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_home_configuration")
@ApiModel(description = "首页配置表")
public class HomeCollocation extends BaseEntity {


    @Column(name = "user_id")
    @Desc(value = "操作人ID")
    @ApiModelProperty("操作人ID")
    private String userId;

    @Column(name = "masterpiece_ids")
    @Desc(value = "配置模块ID，以“,“分割")
    @ApiModelProperty("配置模块ID，以“,“分割")
    private String masterpieceIds;
}
