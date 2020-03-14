package com.dangjia.acg.dto.engineer;

import lombok.Data;

import javax.persistence.Transient;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 11:01
 */
@Data
public class DjMaintenanceRecordProductDTO {

    private String id;
    private String productId;//商品id
    private String maintenanceRecordId;//维保记录表id
    private Double shopCount;//数量
    private String productName;//商品名称
    private Double totalPrice;//销售价格
    private String image;//商品图片
    private String imageUrl;
    private Double price;

    private String valueIdArr;//属性选项选中值id集合
    private String valueNameArr;//商品规格

    private String prodTemplateId;//商品模板id
    private String storefrontProductId;//店铺商品id
}
