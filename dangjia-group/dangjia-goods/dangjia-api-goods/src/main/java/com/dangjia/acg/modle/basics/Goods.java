package com.dangjia.acg.modle.basics;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * @ClassName: Goods
 * @Description: 货品对象
 * @author: zmj
 * @date: 2018-9-18上午9:34:42
 */
@Data
@Entity
@Table(name = "dj_basics_goods")
@ApiModel(description = "货品")
public class Goods extends BaseEntity{

	@Column(name = "name")
    private String name;

	@Column(name = "category_id")
    private String categoryId;//分类id

	@Column(name = "type")
    private Integer type;//0:材料；1：服务

	@Column(name = "buy")
    private Integer buy;//购买性质0：必买；1可选；2自购

	@Column(name = "sales")
    private Integer sales;//退货性质0：可退；1不可退

	@Column(name = "unit_id")
    private String unitId;//单位


}