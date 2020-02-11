package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/12/21 0021
 * Time: 19:32
 */
@Data
public class   OrderSplitItemDTO {
    private String productName;//名字
    private Double num;//本次发货数量
    private Double cost;// 成本价
    private Double supCost;//选择的供应商提供的单价
    private Double supPorterage;//选择的搬运费单价
    private Double supStevedorageCost;//搬运费
    private Double supTotalPrice;//供应商品总价
    private String unitName;//单位
    private Double totalPrice; //总价
    private String brandName;//品牌名

    private String brandSeriesName;//品牌系列

    private String image;
    private Double askCount;//要货数量
    private Double shopCount;//购买总数
    private Double receive;//收货数量

    private String supId;//供应商ID

    private  String orderSplitId;//要货单ID

    private  String orderSplitItemId;//要货单详情ID

    private String storefrontId;//店铺ID

    private String storefrontIcon;//店铺ID

    private String productId;//商品ID

    private String productTemplateId;//商品模板ID

    private String imageUrl;//商品图片访问全地址

    private String productSn;//商品编码

    private String productType;//商品类型(0材料，1服务，2人工）

    private Double price;//单价

    private Double returnCount;//已退量

    private Double surplusCount;//剩余量（可退量）

    private Double stevedorageCost;//搬运费

    private Double transportationCost;//运费

    private String unitId;//单位ID

    private String ConvertUnit;//换算单位ID

    private String brandId;//品牌ID

    private  String valueIdArr;//商品规格ID

    private String valueNameArr;//商品规格名称

    private String categoryId;//分类ID

    private String isUpstairsCost;//是否按1层收取上楼费

    private Double moveCost;//每层搬运费

    private int unitType;//单位数值类型 1=整数单位，2=小数单位

    private String isDeliveryInstall;//是否施工与安装分开



    private List<Map<String,Object>> supplierIdlist;//供应商列表

}
