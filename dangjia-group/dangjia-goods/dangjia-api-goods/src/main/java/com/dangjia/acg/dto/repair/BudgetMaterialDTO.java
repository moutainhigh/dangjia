package com.dangjia.acg.dto.repair;

import lombok.Data;

/**
 * author: Ronalcheng
 * Date: 2018/12/7 0007
 * Time: 14:10
 */
@Data
public class BudgetMaterialDTO {

    private String id;

    private String productId;//货号ID

    private String productSn;// 货号编号

    private String productName;//货号名称

    private String productNickName;//货品昵称

    private double price;// 销售价

    private double cost;// 成本价

    private double shopCount;//购买总数

    private String unitName;//单位

    private double totalPrice; //总价

    private int productType; //0：材料；1：服务

    private String categoryId;//分类id

    private String image;//货品图片
}
