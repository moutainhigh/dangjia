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
    private Integer type;//0:材料；1：包工包料2：人工；3：体验；4：增值

	@Column(name = "buy")
    private Integer buy;//购买性质0：必买；1可选；2自购

	@Column(name = "sales")
    private Integer sales;//退货性质0：可退；1不可退

	@Column(name = "unit_id")
    private String unitId;//单位

    @Column(name = "other_name")
    private String otherName;//货品别名

    @Column(name = "is_influe_decoration_progress")
    private String isInflueDecorationProgress;//是否影响装修进度(1是，0否)

    @Column(name = "irreversible_reasons")
    private String irreversibleReasons;//不可退原因

    @Column(name = "istop")
    private String istop;//是否置顶 0=正常，1=置顶

    @Column(name = "brand_id")
    private String brandId;//品牌id

    @Column(name = "is_elevator_fee")
    private String isElevatorFee;//电梯房是否按1层收取上楼费(1是，0否)

    @Column(name = "indicative_price")
    private Double indicativePrice;//参考价格

    @Column(name = "label_ids")
    private String labelIds;//标签id，多个用逗号分隔

    @Column(name = "is_reservation_deliver")
    private String isReservationDeliver;//是否业主预约发货(1是，0否)


}