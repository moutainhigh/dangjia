package com.dangjia.acg.dto.sale.rob;

import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *抢单 返回参数
 */
@Data
public class RobDTO {

    protected String id;

    @ApiModelProperty("创建时间")
    protected Date createDate;// 创建日期

    @ApiModelProperty("修改时间")
    protected Date modifyDate;// 修改日期

    @ApiModelProperty("数据状态 0=正常，1=删除")
    protected Integer dataStatus;

    @ApiModelProperty("线索id")
    private String clueId;

    @ApiModelProperty("mcid")
    private String mcId;

    @ApiModelProperty("阶段 0:新线索 1：继续跟进 2：已放弃 3：黑名单 4:转客服")
    private Integer stage;

    @ApiModelProperty("阶段 0:线索阶段 1:客户阶段")
    private Integer phaseStatus;

    @ApiModelProperty("业主名称")
    private String owerName;

    @ApiModelProperty("业主电话")
    private String phone;

    @ApiModelProperty("0待确认开工,1装修中,2休眠中,3已完工")
    private Integer visitState;

    @ApiModelProperty("标签id")
    private String labelIdArr;

    @ApiModelProperty("客戶id")
    private String memberId;

    @ApiModelProperty("房子id")
    private String houseId;

    @ApiModelProperty("城市ID")
    private String cityId;

    @ApiModelProperty("小区id")
    private String villageId;

    @ApiModelProperty("销售id")
    private String cusService;

    @ApiModelProperty("抢单状态")
    private Integer isRobStats;

    @ApiModelProperty("已抢单id")
    private String alreadyId;


    @ApiModelProperty("标签名称")
    private List<SaleMemberLabelDTO> list=new ArrayList<>();//标签


    public String getVisitStateName() {
        if(null != getVisitState() && 0 == getVisitState()){
            return "待确认开工";
        }
        return null;
    }

}
