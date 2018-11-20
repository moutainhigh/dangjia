package com.dangjia.acg.modle.basics;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 人工商品
 * @author Ronalcheng
 *
 */
@Data
@Entity
@Table(name = "dj_basics_worker_goods")
@ApiModel(description = "人工商品")
public class WorkerGoods extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "worker_goods_sn")
    private String workerGoodsSn;//人工商品编号

    @Column(name = "image")
    private String image;//图片  多图

    @Column(name = "unit_id")
    private String unitId;//单位id

    @Column(name = "price")
    private Double price;//单价

    @Column(name = "sales")
    private Integer sales;//退货性质

    @Column(name = "work_explain")
    private String workExplain;//工作说明

    @Column(name = "worker_dec")
    private String workerDec;//商品介绍图片

    @Column(name = "worker_standard")
    private String workerStandard;//工艺标准    暂不用(预留)

    @Column(name = "worker_type_id")
    private String workerTypeId;//关联工种id

    @Column(name = "show_goods")
    private Integer showGoods;//是否展示0展示；1不展示


}