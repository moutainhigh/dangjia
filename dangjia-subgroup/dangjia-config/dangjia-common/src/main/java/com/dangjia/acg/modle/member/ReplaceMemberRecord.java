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

/**
 *ljl
 */
@Data
@Entity
@Table(name = "dj_replace_member_record")
@ApiModel(description = "更换工匠历史记录")
@FieldNameConstants(prefix = "")
public class ReplaceMemberRecord extends BaseEntity {

    @Column(name = "member_id")
    @Desc(value = "工匠id")
    @ApiModelProperty("工匠id")
    private String memberId;


}
