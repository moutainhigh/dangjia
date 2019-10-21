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
 * Time: 下午 2:23
 */
@Data
@Entity
@Table(name = "dj_supplier")
@ApiModel(description = "供应商")
@FieldNameConstants(prefix = "")
public class DjSupplier extends BaseEntity {

    @Column(name = "user_id")
    @Desc(value = "用户ID")
    @ApiModelProperty("用户ID")
    private String userId;//用户ID

    @Column(name = "city_id")
    @Desc(value = "城市ID")
    @ApiModelProperty("城市ID")
    private String cityId;//城市ID

    @Column(name = "name")
    @Desc(value = "供应商名称")
    @ApiModelProperty("供应商名称")
    private String name;//供应商名称

    @Column(name = "address")
    @Desc(value = "供应商地址")
    @ApiModelProperty("供应商地址")
    private String address;//供应商地址

    @Column(name = "check_people")
    @Desc(value = "联系人姓名")
    @ApiModelProperty("联系人姓名")
    private String checkPeople;//联系人姓名

    @Column(name = "telephone")
    @Desc(value = "联系电话")
    @ApiModelProperty("联系电话")
    private String telephone;//联系电话

    @Column(name = "email")
    @Desc(value = "邮箱")
    @ApiModelProperty("邮箱")
    private String email;//邮箱

    @Column(name = "create_by")
    @Desc(value = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;//创建人

    @Column(name = "update_by")
    @Desc(value = "修改人")
    @ApiModelProperty("修改人")
    private String updateBy;//修改人

    @Column(name = "is_non_platform_supplier")
    @Desc(value = "是否非平台供应商（1是，0否）")
    @ApiModelProperty("是否非平台供应商（1是，0否）")
    private String isNonPlatformSupperlier;

}
