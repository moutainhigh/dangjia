package com.dangjia.acg.dto.deliver;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BudgetOrderDTO {

    private  String orderId;//订单ID

    private String storefrontId;//店铺ID

    private String houseId;//房子ID

    private String addressId;//地址ID

    private String storefrontName;//店铺名称

    private String storefrontIcon;//店铺图标

    private String orderSource;//订单来源(1,精算制作，2购物车，3补货单，4补差价订单）

    private Date createDate;//订单创建时间

    private Date orderPayTime;//订单创建时间

    private String orderNumber;//订单编号

    private Double actualTotalAmount;//实付总额

    private Double totalAmount;//订单总额

    private String totalRransportationCostRemark;//可退运费描述

    private String totalStevedorageCostRemark;//可退搬运费描述

    private List<BudgetOrderItemDTO> orderDetailList;
}
