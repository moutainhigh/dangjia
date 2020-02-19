package com.dangjia.acg.dto.delivery;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: chenyufeng
 * 店铺利润统计-供应商货单详情
 * Date: 17/10/2019
 * Time: 上午 11:27
 */
@Data
public class SupplierDimensionOrderDetailDTO {

    private String splitDevlierId;//发货单ID
    private String shipAddress; // 房子地址
    private String name; // 业主名称
    private String mobile; //联系号码
    private  String  number;//发货单号
    private Date createDate;//要货时间
    private Double income;//收入
    private Double expenditure ;// 支出
    private Double profit;// 利润
    private String houseId;//房子id

}
