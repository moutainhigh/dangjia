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
 * @descrilbe 首页模块配置表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/6/13 11:00 AM
 */
@Data
@Entity
@FieldNameConstants(prefix = "")
@Table(name = "dj_home_masterplate")
@ApiModel(description = "首页模块配置表")
public class HomeMasterplate extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;

    @Column(name = "url")
    @Desc(value = "H5对应的组件名")
    @ApiModelProperty("H5对应的组件名")
    private String url;

    @Column(name = "user_id")
    @Desc(value = "操作人id")
    @ApiModelProperty("操作人id")
    private String userId;
}
