package com.dangjia.acg.modle.supplier;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:07
 */
@Data
@Entity
@Table(name = "dj_register_application")
@ApiModel(description = "供应商注册申请表")
@FieldNameConstants(prefix = "")
public class DjRegisterApplication extends BaseEntity {


    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;//城市id

    @Column(name = "user_name")
    @Desc(value = "用户名")
    @ApiModelProperty("用户名")
    private String userName;//用户名

    @Column(name = "passWord")
    @Desc(value = "密码")
    @ApiModelProperty("密码")
    private String passWord;//密码

    @Column(name = "name")
    @Desc(value = "姓名")
    @ApiModelProperty("姓名")
    private String name;//姓名

    @Column(name = "mobile")
    @Desc(value = "联系电话")
    @ApiModelProperty("联系电话")
    private String mobile;//联系电话

    @Column(name = "card_number")
    @Desc(value = "身份证号码")
    @ApiModelProperty("身份证号码")
    private String cardNumber;//身份证号码

    @Column(name = "card_image")
    @Desc(value = "身份证照片（正、反面）")
    @ApiModelProperty("身份证照片（正、反面）")
    private String cardImage;//身份证号码

    @Column(name = "business_license")
    @Desc(value = "营业执照")
    @ApiModelProperty("营业执照")
    private String businessLicense;//营业执照

    @Column(name = "application_type")
    @Desc(value = "申请类型 1:店铺 2:供应商")
    @ApiModelProperty("申请类型 1:店铺 2:供应商")
    private String applicationType;//申请类型 1:店铺 2:供应商

    @Column(name = "application_status")
    @Desc(value = "审核状态 0:审核中 1:通过 2:不通过")
    @ApiModelProperty("审核状态 0:审核中 1:通过 2:不通过")
    private Integer applicationStatus;//审核状态 0:审核中 1:通过 2:不通过

    @Column(name = "fail_reason")
    @Desc(value = "失败原因")
    @ApiModelProperty("失败原因")
    private String failReason;//失败原因

    @Column(name = "audit_user_id")
    @Desc(value = "审核人ID")
    @ApiModelProperty("审核人ID")
    private String auditUserId;//审核人ID
}
