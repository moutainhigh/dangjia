package com.dangjia.acg.dto.sale.rob;

import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;

/**
 *抢单详情 返回参数
 */
@Data
@ApiModel(description = "抢单详情 ")
@Entity
public class RobInfoDTO {

    private String id;

    @ApiModelProperty("创建时间")
    private Date createDate;// 创建日期

    @ApiModelProperty("创建时间")
    private Date mcCreateDate;// 创建日期

    @ApiModelProperty("创建时间")
    private Date houseCreateDate;// 创建日期

    @ApiModelProperty("竣工时间")
    private Date completedDate;// 日期

    @ApiModelProperty("修改时间")
    private Date modifyDate;// 修改日期

    @ApiModelProperty("数据状态 0=正常，1=删除")
    private Integer dataStatus;

    @ApiModelProperty("下单时间")
    private Date constructionDate;// 创建日期

    @ApiModelProperty("销售id")
    private String userId;

    @ApiModelProperty("客户id")
    private String memberId;

    @ApiModelProperty("销售id")
    private String cusService;

    @ApiModelProperty("线索id")
    private String clueId;

    @ApiModelProperty("客户基础id")
    private String mcId;

    @ApiModelProperty("房子id")
    private String houseId;

    @ApiModelProperty("业主名称")
    private String owerName;

    @ApiModelProperty("业主电话")
    private String phone;

    @ApiModelProperty("微信")
    private String wechat;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("楼栋号")
    private String houseBuilding;

    @ApiModelProperty("楼栋号")
    private String houseNumber;

    @ApiModelProperty("意向房子")
    private String address;

    @ApiModelProperty("销售录入装修的房子类型0：新房；1：老房")
    private Integer houseType;

    @ApiModelProperty("业主录入装修的房子类型0：新房；1：老房")
    private Integer mcHouseType;

    @ApiModelProperty("有无图纸0：无图纸；1：有图纸")
    private Integer drawings;

    @ApiModelProperty("楼栋号")
    private String building;

    @ApiModelProperty("房号")
    private String number;

    @ApiModelProperty("装修小区名称")
    private String villageName;

    @ApiModelProperty("小区id")
    private String villageId;

    @ApiModelProperty("城市名称")
    private String cityName;

    @ApiModelProperty("城市id")
    private String cityId;

    @ApiModelProperty("阶段 0:线索阶段 1:客户阶段")
    private String phaseStatus;

    @ApiModelProperty("标签id")
    private String labelIdArr;

    @ApiModelProperty("建筑面积")
    private Double buildSquare;
    @ApiModelProperty("设计风格")
    private String style;
    @ApiModelProperty("外框面积")
    private Double square;
    @ApiModelProperty("装修类型: 0表示没有开始，1远程设计，2自带设计，3共享装修")
    private Integer decorationType;
    @ApiModelProperty("0待确认开工,1装修中,2休眠中,3已完工")
    private Integer visitState;


    @ApiModelProperty("小区名")
    private String residential;
    @ApiModelProperty("单元号")
    private String unit;

    @ApiModelProperty("阶段: 0未跟进,1继续跟进,2放弃跟进,3黑名单,4已下单")
    private Integer stage;

    @ApiModelProperty("线索类型 1：跨域下单  0：正常")
    private Integer clueType;

    @ApiModelProperty("大管家信息")
    WorkerTypeDTO workerTypeDTO;

    @ApiModelProperty("")
    private String addressName;

    @ApiModelProperty("标签id")
    private String labelId;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("头像")
    private String head;

    @ApiModelProperty("状态")
    private Integer isRobStats;

    @ApiModelProperty("分配销售 1：已分配  0：未分配")
    private Integer branchUser;

    public String getDrawingsName(){
        if(null != getDrawings() && 0 == getDrawings()){
            return "无图纸";
        }else{
            return "有图纸";
        }

    }

    public String getHouseName() {
        return (CommonUtil.isEmpty(getResidential()) ? "*" : getResidential())
                + (CommonUtil.isEmpty(getBuilding()) ? "*" : getBuilding()) + "栋"
                + (CommonUtil.isEmpty(getUnit()) ? "*" : getUnit()) + "单元"
                + (CommonUtil.isEmpty(getNumber()) ? "*" : getNumber()) + "号";
    }


    public String getDecorationTypeName() {
        if (null != getDecorationType() && 1 == getDecorationType()) {
            return "表示没有开始";
        } else if (null != getDecorationType() && 2 == getDecorationType()) {
            return "远程设计";
        } else if (null != getDecorationType() && 3 == getDecorationType()) {
            return "自带设计";
        } else if (null != getDecorationType() && 4 == getDecorationType()) {
            return "共享装修";
        }
            return "";
    }

    public String getVisitStateName() {
        if(null != getVisitState() && 1 == getVisitState()){
            return "装修中";
        }
        if(null != getVisitState() && 3 == getVisitState()){
            return "已完工";
        }
        return null;
    }

   /* @ApiModelProperty("标签名称")
    private List<SaleMemberLabelDTO> list;

    @ApiModelProperty("沟通记录")
    private List<CustomerRecordInFoDTO> data;*/
}
