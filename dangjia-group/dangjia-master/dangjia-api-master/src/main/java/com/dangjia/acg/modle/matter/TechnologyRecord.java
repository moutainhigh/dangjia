package com.dangjia.acg.modle.matter;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *  工艺节点记录
 */
@Data
@Entity
@Table(name = "dj_matter_technology_record")
@ApiModel(description = "验收工艺记录")
@FieldNameConstants(prefix = "")
public class TechnologyRecord extends BaseEntity {

    @Column(name = "house_id")
    private String houseId;

    @Column(name = "technology_id")
    private String technologyId;//工艺id

    @Column(name = "name")
    private String name;

    @Column(name = "material_or_worker")
    private Integer materialOrWorker;//0材料工艺,1人工工艺

    @Column(name = "worker_type_id")
    private String workerTypeId;//工种id 如果是人工工艺有工种id

    @Column(name = "image")
    private String image;//图片

    @Column(name = "state")
    private Integer state;//验收状态0:未验收;1:已验收,2 不通过
}
