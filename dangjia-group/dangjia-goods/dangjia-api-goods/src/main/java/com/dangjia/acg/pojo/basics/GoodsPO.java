package com.dangjia.acg.pojo.basics;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.modle.attribute.GoodsCategory;
import com.dangjia.acg.modle.basics.Goods;
import com.dangjia.acg.modle.brand.Unit;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;

/**
 * @ClassName: Goods
 * @Description: 商品对象
 * @author: ysl
 * @date: 2018-12-18上午9:34:42
 */
@Data
@Entity
@Table(name = "dj_basics_goods")
@ApiModel(description = "商品")
public class GoodsPO extends Goods {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private GoodsCategory goodsCategory;//分类对象

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "unit_id")
    private Unit unit;//单位对象
}