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
 * @descrilbe 上传设计图推荐的商品记录表
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/10 6:49 PM
 */
@Data
@Entity
@Table(name = "dj_design_quantity_room_product")
@ApiModel(description = "上传设计图推荐的商品记录表")
@FieldNameConstants(prefix = "")
public class DesignQuantityRoomProduct extends BaseEntity {


    @Column(name = "product_id")
    @Desc(value = "商品ID")
    @ApiModelProperty("商品ID")
    private String productId;


    @Column(name = "house_id")
    @Desc(value = "房子ID")
    @ApiModelProperty("房子ID")
    private String houseId;


    @Column(name = "type")
    @Desc(value = "推荐商品类型：0:纯推荐，1:推荐商品支付")
    @ApiModelProperty("推荐商品类型：0:纯推荐，1:推荐商品支付")
    private Integer type;
}
