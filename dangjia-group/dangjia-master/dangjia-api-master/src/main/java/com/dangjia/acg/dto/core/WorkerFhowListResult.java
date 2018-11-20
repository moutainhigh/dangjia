package com.dangjia.acg.dto.core;

import lombok.Data;

@Data
public class WorkerFhowListResult {
	private String houseFlowId;//进程id
	private int houseFlowtype;//进程类型4拆除，5打孔，6水电工，7防水，8泥工,9木工，10油漆工，11安装
	private String houseFlowName;//进程名称
	private String workerId;//工匠id
	private String workerName;//工匠名字
	private String workerPhone;//工匠电话
	private String patrolSecond;//巡查次数
	private String patrolStandard;//巡查标准
	private String isStart;//是否开工0:未开工；1：已开工；
	private Integer state;//装修进度0：未进场；1：待业主支付；2：待交底；3：施工中；4：阶段完工；5：收尾施工；6：整体完工
    private String detailUrl;//进程详情链接
    private String buttonTitle;//审核按钮提示

}
