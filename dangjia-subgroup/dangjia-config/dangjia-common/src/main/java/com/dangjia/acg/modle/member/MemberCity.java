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

@Data
@Entity
@Table(name = "dj_member_city")
@FieldNameConstants(prefix = "")
@ApiModel(description = "当家用户地域记录")
public class MemberCity extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "用户ID")
    @ApiModelProperty("用户ID")
    private String memberId;

    @Column(name = "city_id")
    @Desc(value = "城市id")
    @ApiModelProperty("城市id")
    private String cityId;

    @Column(name = "city_name")
    @Desc(value = "城市名")
    @ApiModelProperty("城市名")
    private String cityName;

}
