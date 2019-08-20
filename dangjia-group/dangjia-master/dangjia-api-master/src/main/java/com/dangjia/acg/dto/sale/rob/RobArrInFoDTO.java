package com.dangjia.acg.dto.sale.rob;

import com.dangjia.acg.dto.member.CustomerRecordInFoDTO;
import com.dangjia.acg.dto.member.IntentionHouseDTO;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.sale.achievement.UserAchievementDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *抢单详情 返回参数
 */
@ApiModel(description = "抢单详情list")
@Data
public class RobArrInFoDTO {
    @ApiModelProperty("有无图纸0：无图纸；1：有图纸")
    private Integer drawings;

    @ApiModelProperty("业主名称")
    private String owerName;

    @ApiModelProperty("业主电话")
    private String phone;

    @ApiModelProperty("微信")
    private String wechat;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("客户id")
    private String memberId;

    @ApiModelProperty("线索id")
    private String clueId;

    @ApiModelProperty("客户基础id")
    private String mcId;

    @ApiModelProperty("阶段 0:线索阶段 1:客户阶段")
    private String phaseStatus;

    @ApiModelProperty("销售id")
    private String userId;

    @ApiModelProperty("订单id")
    private String alreadyId;

    @ApiModelProperty("创建时间")
    private Date createDate;// 创建日期

    @ApiModelProperty("创建时间")
    private Date mcCreateDate;// 创建日期

    @ApiModelProperty("创建时间")
    private Date houseCreateDate;// 创建日期

    @ApiModelProperty("阶段 0:新线索 1：继续跟进 2：已放弃 3：黑名单 4:转客服")
    private Integer stage;

    @ApiModelProperty("")
    private Integer isRobStats;

    @ApiModelProperty("标签名称")
    private List<SaleMemberLabelDTO> list;

    @ApiModelProperty("沟通记录")
    private List<CustomerRecordInFoDTO> data;

    @ApiModelProperty("客户房屋信息")
    private List<RobInfoDTO> customerList;

    @ApiModelProperty("销售业绩")
    List<UserAchievementDTO> userInFo;

    @ApiModelProperty("意向房屋信息")
    private List<IntentionHouseDTO> intentionHouseList;


}
