package com.dangjia.acg.modle.basics;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 标签实体
 * @author ysl
 */
@Data
@Entity
@Table(name = "dj_basics_label")
@ApiModel(description = "标签")
public class Label extends BaseEntity {

    @Column(name = "name")
    private String name;//标签名称

}