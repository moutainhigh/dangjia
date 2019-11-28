package com.dangjia.acg.modle.config;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 实体类 - 短信记录表
 */
@Data
@Entity
@Table(name = "dj_sms")
@ApiModel(description = "APP版本表")
public class Sms extends BaseEntity {

    @Column(name = "mobile")
    @Desc(value = "手机号")
    @ApiModelProperty("手机号")
    private String mobile;

    @Column(name = "code")
    @Desc(value = "验证码")
    @ApiModelProperty("验证码")
    private String code;

}