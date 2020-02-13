package com.dangjia.acg.dto.matter;

import lombok.Data;

import javax.persistence.Column;

/**
 * author: Ronalcheng
 * Date: 2018/12/26 0026
 * Time: 15:00
 */
@Data
public class TechnologyRecordDTO {
    private String id;//节点ID
    private String name;//节点名称
    private Integer state;//验收状态0:未验收;1:已验收,2:已退, 3:勾选中


    private String productId;

    @Column(name = "worker_type_id")
    private String workerTypeId;//工种id 如果是人工工艺有工种id

    @Column(name = "image")
    private String image;//图片
}
