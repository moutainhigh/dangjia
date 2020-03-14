package com.dangjia.acg.modle.user;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@Entity
@Table(name = "dj_user_domain")
@ApiModel(description = "域名表")
public class MainDomain extends BaseEntity {

    @Column(name = "domain_path")
    @Desc(value = "域名地址")
    @ApiModelProperty("域名地址")
    private String domainPath;

    @Column(name = "name")
    @Desc(value = "域名名称")
    @ApiModelProperty("域名名称")
    private String name;


}