package com.dangjia.acg.dto.worker;

import com.dangjia.acg.modle.matter.WorkerDisclosure;
import lombok.Data;

import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/28 0028
 * Time: 17:51
 *
 * 进程详情
 */
@Data
public class CourseDTO {

    private String houseFlowId;
    private String houseFlowApplyId;
    private String rewardUrl;//奖罚url
    private int applyType;//1阶段完工申请，2整体完工申请,3停工申请, 0没有申请
    private int workType; //工序状态: 1还没有发布,2等待被抢,3有工匠抢单,4已采纳已支付
    private int workSteta;//工程状态: 0未开始 ，1阶段完工通过，2整体完工通过，3待交底，4施工中，5收尾施工
    private String houseName;//房产地址
    private WorkerDetailDTO workerDetailDTO;

    private List<WorkerDisclosure> workerDisclosureList;//交底事项
}
