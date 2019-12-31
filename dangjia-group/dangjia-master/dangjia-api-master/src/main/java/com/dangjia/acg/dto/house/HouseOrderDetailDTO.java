package com.dangjia.acg.dto.house;

import lombok.Data;


/**
 * author: fzh
 * Date: 2019/12/11
 * Time: 16:55
 */
@Data
public class HouseOrderDetailDTO {
    private String orderId;
    private String orderItemId;
    private String houseId;//房子ID
    private String workerTypeId;//工种ID
    private String image;//图片
    private String imageUrl;//图片地址
    private String productName;//商品名称
    private Double price;// 单价
    private Double totalPrice;//总价
    private Double shopCount;// 购买数量
    private String productTemplateId;// 商品模板ID
    private String productId;//商品编码
    private String valueIdArr;//商品规格ID
    private String valueNameArr;//商品规格名称
    private String unitId;//单位ID
    private String unitName;//单位名称
    private String orderNumber;//订单编号
    private String orderStatus;//订单状态
    private Integer totalNodeNumber;//总结点数
    private Integer completedNodeNumber;//已完成节点

}
