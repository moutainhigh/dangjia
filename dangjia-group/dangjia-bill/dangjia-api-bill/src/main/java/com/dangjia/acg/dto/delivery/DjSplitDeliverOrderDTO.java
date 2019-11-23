package com.dangjia.acg.dto.delivery;

import com.dangjia.acg.modle.deliver.OrderSplitItem;
import lombok.Data;

import java.util.List;

@Data
public class DjSplitDeliverOrderDTO {
    private String id;//订单id
    private String storefrontName;//店铺名称
    private String createDate;//创建时间
    private String number;//订单号
    private String shippingState;//发货单订单状态
    private String totalAmount;//总价格
    private String total;//件数
    List<OrderSplitItem> orderSplitItemlist;
    private Integer itemListSize;

}
