package com.dangjia.acg.dto.sale.rob;

import com.dangjia.acg.dto.member.CustomerRecordInFoDTO;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import java.util.List;

/**
 *抢单详情 返回参数
 */
@Data
@ApiModel(description = "抢单详情 ")
@Entity
public class UserInfoDTO {

    @ApiModelProperty("销售id")
    private String userId;

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

    @ApiModelProperty("标签id")
    private String labelId;
    @ApiModelProperty("用户名")
    private String userName;
    @ApiModelProperty("头像")
    private String head;

    @ApiModelProperty("标签名称")
    private List<SaleMemberLabelDTO> list;

    @ApiModelProperty("沟通记录")
    private List<CustomerRecordInFoDTO> data;
}
