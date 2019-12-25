package com.dangjia.acg.dto.engineer;

import lombok.Data;


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

    private String valueIdArr;//属性选项选中值id集合
    private String valueNameArr;//商品规格
}
