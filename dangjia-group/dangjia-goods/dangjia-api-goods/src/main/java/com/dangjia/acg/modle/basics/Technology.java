package com.dangjia.acg.modle.basics;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 工艺说明
 *
 * @ClassName: Technology
 * @Description: TODO
 * @author: zmj
 * @date: 2018-9-19下午3:33:07
 */
@Data
@Entity
@Table(name = "dj_basics_technology")
@ApiModel(description = "工艺说明")
public class Technology extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "material_or_worker")
    private Integer materialOrWorker;//0:材料工艺;1:人工工艺

    @Column(name = "worker_type_id")
    private String workerTypeId;//工种id

    @Column(name = "content")
    private String content;//内容

    @Column(name = "type")
    private Integer type;//是否为验收节点0：否；1:是

    @Column(name = "image")
    private String image;//图片


}
