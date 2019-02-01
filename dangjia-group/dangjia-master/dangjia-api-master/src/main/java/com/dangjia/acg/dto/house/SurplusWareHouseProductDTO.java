package com.dangjia.acg.dto.house;

import lombok.Data;

import java.util.Date;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 14:41
 * 剩余材料的临时仓库的挪货记录 详情
 */
@Data
public class SurplusWareHouseProductDTO {

    private String surplusWareHouseId;//临时仓库id
    private String productId;//货品id
    private String productName;//货品名字
    private Integer productCount;//商品的剩余数量
    private String address;// 仓库地址
//    private String productUnit;//货品单位
//    private Date createDate;// 创建日期
//    private Date modifyDate;// 修改日期


    private Integer surplusWareHouseAllCount;//仓库总数
    private Integer productAllCount;//商品总库存
    private Date minDivertDate;// 最近挪货时间

}
