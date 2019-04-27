package com.dangjia.acg.modle.design;

import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.BaseEntity;
import com.dangjia.acg.common.util.CommonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Ruking.Cheng
 * @descrilbe 设计相关操作记录图片储存表（目前就只有量房）
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/4/27 11:56 AM
 */
@Data
@Entity
@Table(name = "dj_design_design_image_type")
@FieldNameConstants(prefix = "")
@ApiModel(description = "设计相关操作记录图片储存表（目前就只有量房）")
public class QuantityRoomImages extends BaseEntity {

    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;

    @Column(name = "quantity_room_id")
    @Desc(value = "操作ID")
    @ApiModelProperty("操作ID")
    private String quantityRoomId;

    @Column(name = "name")
    @Desc(value = "名称")
    @ApiModelProperty("名称")
    private String name;

    @Column(name = "image")
    @Desc(value = "图片地址")
    @ApiModelProperty("图片地址")
    private String image;


    //所有图片字段加入域名和端口，形成全路径
    public void initPath(String imageAddress) {
        this.image = CommonUtil.isEmpty(this.image) ? null : imageAddress + this.image;
    }

}
