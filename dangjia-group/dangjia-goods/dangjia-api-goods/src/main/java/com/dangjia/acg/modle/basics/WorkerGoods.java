package com.dangjia.acg.modle.basics;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 人工商品
 * @author Ronalcheng
 *
 */
@Data
@Entity
@Table(name = "dj_basics_worker_goods")
@ApiModel(description = "人工商品")
@FieldNameConstants(prefix = "")
public class WorkerGoods extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "worker_goods_sn")
    private String workerGoodsSn;//人工商品编号

    @Column(name = "image")
    private String image;//图片  多图

    @Column(name = "unit_name")
    private String unitName;//单位

    @Column(name = "unit_id")
    private String unitId;//单位id

    @Column(name = "price")
    private Double price;//单价

    @Column(name = "sales")
    private Integer sales;//退货性质0：可退；1不可退

    @Column(name = "work_explain")
    private String workExplain;//工作说明

    @Column(name = "worker_dec")
    private String workerDec;//商品介绍图片

    @Column(name = "worker_standard")
    private String workerStandard;//工艺标准    暂不用(预留)

    @Column(name = "worker_type_id")
    private String workerTypeId;//关联工种id

    @Column(name = "show_goods")
    private Integer showGoods;//是否展示 1展示；0不展示
//    private Integer showGoods;//是否展示 0展示；1不展示

    @Column(name = "other_name")
    private String otherName;//人工商品别名


    @Column(name = "last_price")
    @Desc(value = "调后单价")
    @ApiModelProperty("调后单价")
    private Double LastPrice;//调后单价

    @Column(name = "last_time")
    @Desc(value = "调价时间")
    @ApiModelProperty("调价时间")
    private Date LastTime;//调价时间

    @Column(name = "technology_ids")
    @Desc(value = "关联的工艺ID")
    @ApiModelProperty("关联的工艺ID，多个逗号分割")
    private String technologyIds;//关联的工艺ID，多个逗号分割

    @Column(name = "considerations")
    @Desc(value = "注意事项")
    @ApiModelProperty("注意事项")
    private String considerations;//注意事项

    @Column(name = "calculate_content")
    @Desc(value = "计价说明")
    @ApiModelProperty("计价说明，json串")
    private String calculateContent;

    @Column(name = "build_content")
    @Desc(value = "施工说明")
    @ApiModelProperty("施工说明，json串")
    private String buildContent;


}