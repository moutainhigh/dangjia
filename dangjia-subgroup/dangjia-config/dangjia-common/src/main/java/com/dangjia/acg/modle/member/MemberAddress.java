package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dj_member_address")
@FieldNameConstants(prefix = "")
@ApiModel(description = "当家用户身份表")
public class MemberAddress extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "会员编号")
    @ApiModelProperty("会员编号")
    private String memberId;// 会员编号

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;// 房子ID

    @Column(name = "renovation_type")
    @Desc(value = "是否是装修地址:0：否，1：是")
    @ApiModelProperty("是否是装修地址:0：否，1：是")
    private Integer renovationType;//是否是装修地址:0：否，1：是

    @Column(name = "default_type")
    @Desc(value = "是否是默认地址:0：否，1：是")
    @ApiModelProperty("是否是默认地址:0：否，1：是")
    private Integer defaultType;// 是否是默认地址:0：否，1：是

    @Column(name = "city_name")
    @Desc(value = "省/市/区")
    @ApiModelProperty("省/市/区")
    private String cityName;// 省/市/区

    @Column(name = "address")
    @Desc(value = "详细地址")
    @ApiModelProperty("详细地址")
    private String address;// 详细地址

    @Column(name = "input_area")
    @Desc(value = "录入面积")
    @ApiModelProperty("录入面积")
    private BigDecimal inputArea;// 录入面积

    @Column(name = "longitude")
    @Desc(value = "经度")
    @ApiModelProperty("经度")
    private String longitude;// 经度

    @Column(name = "latitude")
    @Desc(value = "纬度")
    @ApiModelProperty("纬度")
    private String latitude;// 纬度

    @Column(name = "name")
    @Desc(value = "业主姓名")
    @ApiModelProperty("业主姓名")
    private String name;

    @Column(name = "mobile")
    @Desc(value = "业主手机")
    @ApiModelProperty("业主手机")
    private String mobile;
}
