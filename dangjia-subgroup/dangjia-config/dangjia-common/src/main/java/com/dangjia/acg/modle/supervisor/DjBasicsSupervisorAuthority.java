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

@Data
@Entity
@Table(name = "dj_basics_supervisor_authority")
@ApiModel(description = "督导权限配置表")
@FieldNameConstants(prefix = "")
public class DjBasicsSupervisorAuthority  extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房子id")
    @ApiModelProperty("房子id")
    private String houseId ;

    @Column(name = "address")
    @Desc(value = "工地地址")
    @ApiModelProperty("工地地址")
    private String address ;

    @Column(name = "member_id")
    @Desc(value = "用户id")
    @ApiModelProperty("用户id")
    private String memberId ;

    @Column(name = "name")
    @Desc(value = "业主名称")
    @ApiModelProperty("业主名称")
    private String name ;

    @Column(name = "mobile")
    @Desc(value = "业主手机")
    @ApiModelProperty("业主手机")
    private String mobile ;

    @Column(name = "visitState")
    @Desc(value = "施工状态")
    @ApiModelProperty("施工状态")
    private String visitState ;

    @Column(name = "constructionDate")
    @Desc(value = "开工时间")
    @ApiModelProperty("开工时间")
    private String constructionDate ;

    @Column(name = "operateId")
    @Desc(value = "操作人id")
    @ApiModelProperty("操作人id")
    private String operateId ;


}
