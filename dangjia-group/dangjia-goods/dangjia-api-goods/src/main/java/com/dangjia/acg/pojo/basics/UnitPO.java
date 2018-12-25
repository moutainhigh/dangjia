package com.dangjia.acg.pojo.basics;

import com.dangjia.acg.modle.brand.Unit;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 单位实体
 * @author Ronalcheng
 */
@Data
@Entity
@Table(name = "dj_basics_unit")
@ApiModel(description = "单位")
public class UnitPO extends Unit {

    @Column(name = "name")
    private String name;//名称

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "UnitPO")
    @JoinColumn(name = "attribute_id_arr")
    private List<ProductPO> attributeValueLists;//货品集合
}