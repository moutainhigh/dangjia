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
@Table(name = "dj_member_collect")
@FieldNameConstants(prefix = "")
@ApiModel(description = "当家用户收藏记录")
public class MemberCollect extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "用户ID")
    @ApiModelProperty("用户ID")
    private String memberId;


    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;


    @Column(name = "condition_type")
    @Desc(value = "收藏类型,0:代表收藏房子 1：代表收藏商品")
    @ApiModelProperty("收藏类型")
    private String conditionType;

}
