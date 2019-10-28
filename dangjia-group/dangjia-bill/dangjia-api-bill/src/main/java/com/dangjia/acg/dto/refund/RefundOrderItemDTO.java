package com.dangjia.acg.dto.refund;

import lombok.Data;

import java.util.List;

@Data
public class RefundOrderItemDTO {

    private String orderId;//订单ID

    private String orderItemId;//订单详情ID

    private String productId;//商品ID

    private String productName;//商品名称

    private String productSn;//商品编码

    private String productType;//商品类型(0材料，1服务，2人工）

    private Double shopCount;//购买量

    private Double askCount;//要货量

    private Double surplusCount;//剩余量

    private Double stevedorageCost;//搬运费

    private Double transportationCost;//运费

    private String brandId;//品牌ID

    private String brandName;//品牌名称

    private  String valueIdAttr;//商品规格ID

    private String valueNameAttr;//商品规格名称
}
