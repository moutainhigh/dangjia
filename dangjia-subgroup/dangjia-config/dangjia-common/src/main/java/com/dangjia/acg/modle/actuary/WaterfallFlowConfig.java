package com.dangjia.acg.modle.actuary;

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
 * Created with IntelliJ IDEA.
 * Date: 2019/9/20
 * Time: 16:26
 */
@Data
@Entity
@Table(name = "dj_waterfall_flow_config")
@ApiModel(description = "瀑布流配置")
@FieldNameConstants(prefix = "")
public class WaterfallFlowConfig extends GoodsBaseEntity {

    @Column(name = "parent_id")
    @Desc(value = "父ID")
    @ApiModelProperty("父ID")
    private String parentId;

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "any_id")
    @Desc(value = "任意ID")
    @ApiModelProperty("任意ID")
    private String anyId;

    @Column(name = "type")
    @Desc(value = "类型：1 标签，2商品，3装修说，4装修攻略，5精选案例")
    @ApiModelProperty("类型：1 标签，2商品，3装修说，4装修攻略，5精选案例")
    private Integer type;


    @Column(name = "sort")
    @Desc(value = "排序")
    @ApiModelProperty("排序")
    private Integer sort;

    @Column(name = "ratio")
    @Desc(value = "占比")
    @ApiModelProperty("占比")
    private Integer ratio;

    @Column(name = "remark")
    @Desc(value = "备注")
    @ApiModelProperty("备注")
    private String remark;


}
