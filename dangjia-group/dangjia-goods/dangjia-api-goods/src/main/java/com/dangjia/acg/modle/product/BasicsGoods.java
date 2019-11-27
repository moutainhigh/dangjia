package com.dangjia.acg.modle.product;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.modle.GoodsBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
public class BasicsGoods extends GoodsBaseEntity {

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "category_id")
    @Desc(value = "类别id")
    @ApiModelProperty("类别id")
    private String categoryId;

    @Column(name = "type")
    @Desc(value = "类型0：材料；1：服务；2：人工；3：体验；4：增值")
    @ApiModelProperty("类型0：材料；1：服务；2：人工；3：体验；4：增值")
    private Integer type;

    @Column(name = "buy")
    @Desc(value = "购买性质0：必买；1：可选；2：自购")
    @ApiModelProperty("购买性质0：必买；1：可选；2：自购")
    private Integer buy;

    @Column(name = "sales")
    @Desc(value = "退货性质0：可退；1：不可退")
    @ApiModelProperty("退货性质0：可退；1：不可退")
    private Integer sales;

    @Column(name = "unit_id")
    @Desc(value = "单位id")
    @ApiModelProperty("单位id")
    private String unitId;

    @Column(name = "other_name")
    @Desc(value = "货品别名")
    @ApiModelProperty("货品别名")
    private String otherName;

    @Column(name = "is_influe_decoration_progress")
    @Desc(value = "是否影响装修进度(1是，0否)")
    @ApiModelProperty("是否影响装修进度(1是，0否)")
    private String isInflueDecorationProgress;

    @Column(name = "irreversible_reasons")
    @Desc(value = "不可退原因")
    @ApiModelProperty("不可退原因")
    private String irreversibleReasons;

    @Column(name = "istop")
    @Desc(value = "是否置顶 0=正常，1=置顶")
    @ApiModelProperty("是否置顶 0=正常，1=置顶")
    private String istop;

    @Column(name = "brand_id")
    @Desc(value = "品牌id")
    @ApiModelProperty("品牌id")
    private String brandId;

    @Column(name = "is_elevator_fee")
    @Desc(value = "电梯房是否按1层收取上楼费(1是，0否)")
    @ApiModelProperty("电梯房是否按1层收取上楼费(1是，0否)")
    private String isElevatorFee;

    @Column(name = "indicative_price")
    @Desc(value = "参考价格")
    @ApiModelProperty("参考价格")
    private Double indicativePrice;

    @Column(name = "label_ids")
    @Desc(value = "标签id")
    @ApiModelProperty("标签id")
    private String labelIds;

    @Column(name = "is_reservation_deliver")
    @Desc(value = "是否业主预约发货(1是，0否)")
    @ApiModelProperty("是否业主预约发货(1是，0否)")
    private String isReservationDeliver;//是否业主预约发货(1是，0否)

    @Column(name = "attribute_id_arr")
    @Desc(value = "属性选中值Id集合")
    @ApiModelProperty("属性选中值Id集合")
    private String attributeIdArr;//属性选中值Id集合

    @Column(name="housekeeper_acceptance")
    @Desc(value = "是否需要管家验收（1是，0否）")
    @ApiModelProperty("是否需要管家验收（1是，0否）")
    private Integer housekeeperAcceptance;//是否需要管家验收（1是，0否）

    @Column(name="technology_ids")
    @Desc(value = "关联的工艺ID，多个逗号分割")
    @ApiModelProperty("关联的工艺ID，多个逗号分割")
    private String technologyIds;//关联的工艺ID，多个逗号分割

}