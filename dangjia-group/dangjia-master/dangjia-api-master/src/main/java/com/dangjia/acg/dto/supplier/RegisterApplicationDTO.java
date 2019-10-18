package com.dangjia.acg.dto.supplier;

import com.dangjia.acg.dto.sale.achievement.AchievementInfoDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *门店业绩 返回参数
 */
@Data
public class RegisterApplicationDTO implements Serializable {

    @ApiModelProperty("id")
    private String id;//申请ID

    @ApiModelProperty("城市id")
    private String cityId;//城市ID

    @ApiModelProperty("城市名称")
    private String cityName;//城市名称

    @ApiModelProperty("姓名")
    private String name;//姓名

    @ApiModelProperty("联系电话")
    private String mobile;//联系电话

    @ApiModelProperty("身份证号码")
    private String cardNumber;//身份证号码

    @ApiModelProperty("身份证照片（正、反面）")
    private String cardImage;//身份证号码

    @ApiModelProperty("营业执照")
    private String businessLicense;//营业执照

    @ApiModelProperty("申请类型 1:店铺 2:供应商")
    private String applicationType;//申请类型 1:店铺 2:供应商

    @ApiModelProperty("审核状态 0:审核中 1:通过 2:不通过")
    private Integer applicationStatus;//审核状态 0:审核中 1:通过 2:不通过

    @ApiModelProperty("失败原因")
    private String failReason;//失败原因

    @ApiModelProperty("审核人ID")
    private String auditUserId;//审核人ID

    @ApiModelProperty("申请时间")
    private Date createDate;//审核人ID

}
