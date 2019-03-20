package com.dangjia.acg.modle.member;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "dj_member_auth")
@FieldNameConstants(prefix = "")
@ApiModel(description = "当家用户第三方认证表")
public class MemberAuth extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "当家用户ID")
    @ApiModelProperty("当家用户ID")
    private String memberId;

    @Column(name = "openid")
    @Desc(value = "第三方认证ID")
    @ApiModelProperty("第三方认证ID")
    private String openid;

    @Column(name = "open_type")
    @Desc(value = "认证类型 1:微信，2：QQ，3:新浪，4:支付宝")
    @ApiModelProperty("认证类型 1:微信，2：QQ，3:新浪，4:支付宝")
    private Integer openType;

    @Column(name = "access_token")
    @Desc(value = "第三方接口调用凭证")
    @ApiModelProperty("第三方接口调用凭证")
    private String accessToken;

    @Column(name = "unionid")
    @Desc(value = "只有微信在用户将公众号绑定到微信开放平台帐号后，才会出现该字段")
    @ApiModelProperty("只有微信在用户将公众号绑定到微信开放平台帐号后，才会出现该字段")
    private String unionid;

    @Column(name = "uid")
    @Desc(value = "第三方uid")
    @ApiModelProperty("第三方uid")
    private String uid;

    @Column(name = "name")
    @Desc(value = "第三方所用的昵称")
    @ApiModelProperty("第三方所用的昵称")
    private String name;

    @Column(name = "iconurl")
    @Desc(value = "第三方所使用的头像")
    @ApiModelProperty("第三方所使用的头像")
    private String iconurl;

    @Column(name = "user_role")
    @Desc(value = "app应用角色  1为业主角色，2为工匠角色")
    @ApiModelProperty("app应用角色  1为业主角色，2为工匠角色")
    private Integer userRole;

}
