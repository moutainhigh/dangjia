package com.dangjia.acg.dto.core;

import lombok.Data;

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 21/12/2019
 * Time: 上午 10:32
 * 验收动dto
 */
@Data
public class AcceptanceDynamicDTO {

    private String workerHead;//工人头像
    private String workerTypeName;//工匠类型
    private String workerName;//工人名称
    private String workerContent;//工人内容
    private Integer workerApplyType;//0每日完工申请，1阶段完工申请，2整体完工申请,3停工申请，4：每日开工,5有效巡查,6无人巡查,7追加巡查
    private String [] workerImgArr;//工人图片
    private String houseId;
    private Integer workerStar;//业主对工匠评分
    private String workerOwnerContent;//业主对工匠评价
    private Date createDate;
    private Integer memberCheck;//用户审核结果,0未审核，1审核通过，2审核不通过，3自动审核
    private Integer supervisorCheck;//大管家审核结果,0未审核，1审核通过，2审核不通过
    private String supervisorHead;//大管家头像
    private String supervisorTypeName;//大管家类型
    private String supervisorName;//大管家名称
    private String supervisorContent;//大管家内容
    private String [] supervisorImgArr;//大管家图片
    private Integer supervisorStar;//业主对大管评分
    private String supervisorOwnerContent;//业主对大管评价
    private Integer status;//申述处理状态.0:待处理。1.驳回。2.接受。
    private String workerHouseFlowApplyId;
    private String supervisorHouseFlowApplyId;
    private String houseFlowApplyId;
}
