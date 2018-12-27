package com.dangjia.acg.modle.matter;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 验收工艺记录
 * @ClassName: TechnologyRecord
 *
 * 只验收工艺节点
 */
@Data
@Entity
@Table(name = "dj_matter_technology_record")
@ApiModel(description = "验收工艺记录")
@FieldNameConstants(prefix = "")
public class TechnologyRecord extends BaseEntity {

    @Column(name = "technology_Id")
    private String technologyId;//工艺id

    @Column(name = "name")
    private String name;

    @Column(name = "material_or_worker")
    private Integer materialOrWorker;//0:材料工艺;1:人工工艺

    @Column(name = "worker_type_id")
    private String workerTypeId;//工种id 如果是人工工艺有工种id

    @Column(name = "content")
    private String content;//内容

    @Column(name = "type")
    private Integer type;//是否为验收节点0：否；1:是

    @Column(name = "image")
    private String image;//图片

    @Column(name = "state")
    private Integer state;//验收状态0:未验收;1:已验收,2:已退, 3:勾选中

    @Column(name = "house_flow_id")
    private String houseFlowId;//houseFlowId
}
