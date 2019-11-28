package com.dangjia.acg.modle.config;

import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

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
@FieldNameConstants(prefix = "")
public class ServiceType extends GoodsBaseEntity{

    @Column(name = "name")
    private String name;//名称

    @Column(name = "cover_image")
    private String coverImage;//图片

    @Column(name = "image")
    private String image;//图片
}