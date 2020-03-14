package com.dangjia.acg.modle.design;

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
 * @author Ruking.Cheng
 * @descrilbe 附件上传记录表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/14 2:27 PM
 */
@Data
@Entity
@Table(name = "dj_enclosure")
@ApiModel(description = "附件上传记录表")
@FieldNameConstants(prefix = "")
public class Enclosure extends BaseEntity {


    @Column(name = "name")
    @Desc(value = "文件名称")
    @ApiModelProperty("文件名称")
    private String name;


    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;


    @Column(name = "enclosure")
    @Desc(value = "文件地址")
    @ApiModelProperty("文件地址")
    private String enclosure;


    @Column(name = "enclosure_type")
    @Desc(value = "附件类型0:设计上传的设计图，1:扩展")
    @ApiModelProperty("附件类型0:设计上传的设计图，1:扩展")
    private Integer enclosureType;


    @Column(name = "remarks")
    @Desc(value = "操作描述")
    @ApiModelProperty("操作描述")
    private String remarks;


    @Column(name = "user_type")
    @Desc(value = "操作用户类型0:中台，1:APP")
    @ApiModelProperty("操作用户类型0:中台，1:APP")
    private Integer userType;


    @Column(name = "user_id")
    @Desc(value = "操作用户ID")
    @ApiModelProperty("操作用户ID")
    private String userId;
}
