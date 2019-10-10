package com.dangjia.acg.modle.config;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @类 名： ServiceType.java
 */
@Data
@Entity
@Table(name = "dj_service_type_configuration")
@ApiModel(description = "品牌")
public class ServiceType extends BaseEntity{

    @Column(name = "name")
    private String name;//名称

    @Column(name = "image")
    private String image;//图片
}