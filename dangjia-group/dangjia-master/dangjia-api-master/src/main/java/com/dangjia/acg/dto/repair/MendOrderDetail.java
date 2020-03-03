package com.dangjia.acg.dto.repair;

import com.dangjia.acg.modle.repair.ChangeOrder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/12/24 0024
 * Time: 16:45
 */
@Data
public class MendOrderDetail {
    private String mendOrderId;
    private String number;//单号
    private Integer type;
    private Integer state;
    private Integer isShow;//是否显示重新买回按钮
    private Integer isAuditor;//是否显示审核按钮
    private Double totalAmount;
    private Date createDate;
    private Date modifyDate;
    private String stateName;


    private String houseId;//房ID
    private String houseName;//房名

    private String memberId;//业主ID
    private String memberName;//业主名称
    private String memberMobile;//业主手机号


    private String applicantId;//申请人ID
    private String applicantName;//申请人名称
    private String applicantMobile;//申请人手机号

    private Integer workerType;//工种

    private Integer houseFlowApplyType;//类型 1：主动验收 2：被动验收

    private String workerTypeColor;//工种颜色
    private String workerTypeName;//工种名称

    private ChangeOrder changeOrder;
    private List<Map<String,Object>> mapList;
    private List<Map<String, Object>> workerList;
    private List<String> imageList;

    private List<Map> button;
}
