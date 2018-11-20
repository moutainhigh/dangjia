package com.dangjia.acg.modle.basics;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 人工商品关联工艺说明
 *
 * @ClassName: WorkerTechnology
 * @author: zmj
 * @date: 2018-9-19下午3:38:51
 */
@Data
@Entity
@Table(name = "dj_basics_worker_technology")
@ApiModel(description = "人工商品关联工艺")
public class WorkerTechnology extends BaseEntity {

    @Column(name = "worker_goods_id")
    private String workerGoodsId;//人工商品id

    @Column(name = "technology_id")
    private String technologyId;//工艺说明id

}
