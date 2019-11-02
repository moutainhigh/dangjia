package com.dangjia.acg.dto.refund;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RefundRepairOrderMaterialDTO {

    private String repairMendOrderId;//退款申请单ID

    private String repairOrderMaterialId;//退款申请单详情ID

    private String storefrontId;//店铺ID

    private String productId;//商品ID

    private String productTemplateId;//

    private String productName;//商品名称

    private String image;//商品图片地址

    private String imageUrl;//商品图片访问全地址

    private String productSn;//商品编码

    private String productType;//商品类型(0材料，1服务，2人工）

    private Double price;//单价

    private Double cost;//平均成本价

    private Double returnCount;//退货量

    private Double stevedorageCost;//搬运费

    private Double transportationCost;//运费

    private String unitId;//单位ID

    private String unitName;//单位名称

    private String convertUnit;//换算单位ID

    private String brandId;//品牌ID

    private String brandName;//品牌名称

    private  String valueIdArr;//商品规格ID

    private String valueNameArr;//商品规格名称

    private String categoryId;//分类ID

    private String orderItemId;//订单详情ID
}
