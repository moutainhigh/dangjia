package com.dangjia.acg.modle.recommend;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dj_recommend_target_info")
@ApiModel(description = "推荐目标信息表")
@FieldNameConstants(prefix = "")
public class RecommendTargetInfo extends BaseEntity {

    /** 子参考项id */
    @Column(name = "item_sub_id")
    @Desc(value = "子参考项id")
    @ApiModelProperty("子参考项id")
    private String itemSubId;

    /** 目标类型 0商品，1攻略(指南)，2问答，3案例，4工地*/
    @Column(name = "target_type")
    @Desc(value = "目标类型")
    @ApiModelProperty("目标类型")
    private Integer targetType;

    /** 实际目标内容的id */
    @Column(name = "target_id")
    @Desc(value = "实际目标内容的id")
    @ApiModelProperty("实际目标内容的id")
    private String targetId;

    /** 实际目标内容的名称 */
    @Column(name = "target_name")
    @Desc(value = "实际目标内容的名称")
    @ApiModelProperty("实际目标内容的名称")
    private String targetName;

    /** 图片 */
    @Column(name = "image")
    @Desc(value = "图片")
    @ApiModelProperty("图片")
    private String image;

    /** 排序 */
    @Column(name = "sort")
    @Desc(value = "排序")
    @ApiModelProperty("排序")
    private Integer sort;

    /** 销售量 */
    @Column(name = "click_number")
    @Desc(value = "点击量")
    @ApiModelProperty("点击量")
    private Integer clickNumber;

    /** 商品售价 */
    @Column(name = "sell_price")
    @Desc(value = "商品售价")
    @ApiModelProperty("商品售价")
    private Double sellPrice;

    /** 单位名称 */
    @Column(name = "unit_name")
    @Desc(value = "单位名称")
    @ApiModelProperty("单位名称")
    private String unitName;

    /** 装修状态 */
    @Column(name = "visit_state")
    @Desc(value = "装修状态")
    @ApiModelProperty("装修状态")
    private Integer visitState;
}
