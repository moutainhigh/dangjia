package com.dangjia.acg.modle.supervisor;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Data
@Entity
@Table(name = "dj_basics_supervisor_authority")
@ApiModel(description = "督导权限配置表")
@FieldNameConstants(prefix = "")
public class SupervisorAuthority extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房子id")
    @ApiModelProperty("房子id")
    private String houseId ;

    @Column(name = "member_id")
    @Desc(value = "用户id")
    @ApiModelProperty("用户id")
    private String memberId ;

    @Column(name = "operateId")
    @Desc(value = "操作人id")
    @ApiModelProperty("操作人id")
    private String operateId ;

}
