package com.dangjia.acg.pojo.basics;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.basics.Label;
import com.dangjia.acg.pojo.attribute.AttributePO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * 标签实体
 * @author ysl
 */
@Data
@Entity
@Table(name = "dj_basics_label")
@ApiModel(description = "标签")
public class LabelPO extends Label {

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "LabelPO")
    private List<ProductPO> productPOLists;//货品集合
}