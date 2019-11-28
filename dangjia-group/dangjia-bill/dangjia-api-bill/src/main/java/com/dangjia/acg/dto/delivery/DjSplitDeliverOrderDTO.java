package com.dangjia.acg.dto.delivery;

import com.dangjia.acg.modle.deliver.OrderSplitItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DjSplitDeliverOrderDTO {
    private String id;//订单id
    private String storefrontName;//店铺名称
    private String storefrontIcon;//店铺图标
    private Date createDate;//创建时间
    private String orderNumber;//订单号
    private String shippingState;//按钮状态 1004 不展示按钮  1待收货   7 待安装
    private String shippingType;// 发货单订单状态   10-待收货  11-已完成
    private String totalAmount;//总价格
    private String total;//件数
    List<OrderSplitItem> orderSplitItemlist;
    private Integer productCount;
    private String productImageArr;
    private String houseId;
    private String cityId;
    private String productName;
}
