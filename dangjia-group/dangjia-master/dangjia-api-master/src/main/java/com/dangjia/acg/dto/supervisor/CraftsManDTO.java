package com.dangjia.acg.dto.supervisor;

import lombok.Data;

/**
 *  普通工匠实体(工种名称、姓名、完工状态、工期、节点)
 */
@Data
public class CraftsManDTO {
    private String workerTypeName;//工种名称
    private String name;//姓名
    private String workSteta;//施工状态，0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中，5收尾施工
    private String projectTime;//工期
    private String node;//节点
    private String iamge;//头像
}
