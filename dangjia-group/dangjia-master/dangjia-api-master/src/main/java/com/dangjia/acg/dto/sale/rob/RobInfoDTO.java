package com.dangjia.acg.dto.sale.rob;

import com.dangjia.acg.common.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 *抢单详情 返回参数
 */
@Data
@ApiModel(description = "抢单详情 ")
public class RobInfoDTO {

    private String id;

    @ApiModelProperty("创建时间")
    private Date createDate;// 创建日期

    @ApiModelProperty("修改时间")
    private Date modifyDate;// 修改日期

    @ApiModelProperty("数据状态 0=正常，1=删除")
    private Integer dataStatus;

    @ApiModelProperty("销售id")
    private String userId;

    @ApiModelProperty("客户id")
    private String memberId;

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

    @ApiModelProperty("意向房子")
    private String address;

    @ApiModelProperty("装修的房子类型0：新房；1：老房")
    private Integer houseType;

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

    @ApiModelProperty("小区名")
    private String residential;
    @ApiModelProperty("单元号")
    private String unit;


    public String getHouseName() {
        return (CommonUtil.isEmpty(getResidential()) ? "*" : getResidential())
                + (CommonUtil.isEmpty(getBuilding()) ? "*" : getBuilding()) + "栋"
                + (CommonUtil.isEmpty(getUnit()) ? "*" : getUnit()) + "单元"
                + (CommonUtil.isEmpty(getNumber()) ? "*" : getNumber()) + "号";
    }

   /* @ApiModelProperty("标签名称")
    private List<SaleMemberLabelDTO> list;

    @ApiModelProperty("沟通记录")
    private List<CustomerRecordInFoDTO> data;*/
}
