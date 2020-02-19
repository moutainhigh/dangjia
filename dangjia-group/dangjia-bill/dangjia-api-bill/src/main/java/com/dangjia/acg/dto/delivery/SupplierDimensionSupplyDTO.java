package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: chenyufeng
 * 店铺利润统计-供应商供应详情
 * Date: 17/10/2019
 * Time: 上午 11:27
 */
@Data
public class SupplierDimensionSupplyDTO {
    private String storefrontId;//店铺id
    private String shipAddress; // 房子地址
    private String mobile; //联系号码
    private String name; // 业主名称
    private Double income;//收入
    private String expenditure ;// 支出
    private String profit;// 利润
    private String houseId;//房子id
    private String addressId;//地址ID
    private String supId;//供应商ID
}
