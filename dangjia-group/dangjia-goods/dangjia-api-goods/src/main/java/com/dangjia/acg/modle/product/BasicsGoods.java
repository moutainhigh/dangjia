package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ClassName: Goods
 * @Description: 货品对象
 * @author: fzh
 * @date: 2018-9-12
 */
@Data
@Entity
@Table(name = "dj_basics_goods")
@ApiModel(description = "货品")
@FieldNameConstants(prefix = "")
public class BasicsGoods extends BaseEntity{

	@Column(name = "name")
    private String name;

	@Column(name = "category_id")
    private String categoryId;//分类id

	@Column(name = "type")
    private Integer type;//0:材料；1：包工包料

	@Column(name = "buy")
    private Integer buy;//购买性质0：必买；1可选；2自购

	@Column(name = "sales")
    private Integer sales;//退货性质0：可退；1不可退

	@Column(name = "unit_id")
    private String unitId;//单位

    @Column(name = "other_name")
    private String otherName;//货品别名


}