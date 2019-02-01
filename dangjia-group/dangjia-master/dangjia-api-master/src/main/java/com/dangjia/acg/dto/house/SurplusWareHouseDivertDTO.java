package com.dangjia.acg.dto.house;

import lombok.Data;

import java.util.Date;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 14:41
 * 剩余材料的临时仓库的挪货记录
 */
@Data
public class SurplusWareHouseDivertDTO {
    private String productId;//货品id
    private Integer divertCount;//挪出数量
    private Integer divertType;//挪货去向类型： 1临时仓库 2供应商
    private String srcSurplusWareHouseId;//原仓库id
//    private String srcAddress;//原仓库地址
    private String toSurplusWareHouseId;//挪货去向的临时仓库id
    private String toAddress;//挪货去向
    protected Date divertDate;// 挪货日期
    private Date createDate;// 创建日期
}
