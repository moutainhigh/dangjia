package com.dangjia.acg.modle.config;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 实体类 - APP版本表
 */
@Data
@Entity
@Table(name = "dj_config_app")
@ApiModel(description = "APP版本表")
public class ConfigApp extends BaseEntity {

    @Column(name = "name")
    @Desc(value = "版本名称")
    @ApiModelProperty("版本名称")
    private String name;

    @Column(name = "app_type")
    @Desc(value = "来源应用（1:安卓-业主端，2:安卓-工匠端，3:iOS-业主端，4:iOS-工匠端）")
    @ApiModelProperty("来源应用（1:安卓-业主端，2:安卓-工匠端，3:iOS-业主端，4:iOS-工匠端）")
    private String appType;

    @Column(name = "version")
    @Desc(value = "版本号")
    @ApiModelProperty("版本号")
    private String version;

    @Column(name = "version_code")
    @Desc(value = "版本号Code")
    @ApiModelProperty("版本号Code")
    private int versionCode;

    @Column(name = "detail")
    @Desc(value = "版本描述")
    @ApiModelProperty("版本描述")
    private String detail;


    @Column(name = "is_forced")
    @Desc(value = "是否强制（0：是；1：否）")
    @ApiModelProperty("是否强制（0：是；1：否）")
    private Boolean isForced;

    @Column(name = "url")
    @Desc(value = "上传地址")
    @ApiModelProperty("上传地址")
    private String url;

    //所有图片字段加入域名和端口，形成全路径
    public void initPath(String address){
        this.url= StringUtils.isEmpty(this.url)?null:address+this.url;//二维码
    };
}