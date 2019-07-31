package com.dangjia.acg.dto.sale.rob;

import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.dto.member.CustomerRecordInFoDTO;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 *抢单详情 返回参数
 */
@Data
public class RobInfoDTO extends BaseEntity {

    @ApiModelProperty("线索id")
    private String clueId;

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

    @ApiModelProperty("标签名称")
    private List<SaleMemberLabelDTO> list;

    @ApiModelProperty("沟通记录")
    private List<CustomerRecordInFoDTO> data;
}
